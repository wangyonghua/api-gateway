package com.example.demo.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by wangyonghua on 2017/10/28.
 */
public class ApiGatewayHand implements InitializingBean, ApplicationContextAware {

    APIStore apiStore;
    private static final String METHOD = "method";
    private static final String PARAMS = "params";

    final LocalVariableTableParameterNameDiscoverer parameterUtil;

    public ApiGatewayHand() {
        parameterUtil = new LocalVariableTableParameterNameDiscoverer();
    }

    public void handle(HttpServletRequest req, HttpServletResponse resp) throws ApiException, InvocationTargetException, IllegalAccessException {
        APIStore.ApiRunable apiRunable = sysParameterValidate(req);
        Object[] objects = buildParam(apiRunable, req.getParameter(PARAMS), req, resp);
        Object run = apiRunable.run(objects);
        returnResult(run, resp);
    }

    private APIStore.ApiRunable sysParameterValidate(HttpServletRequest request) throws ApiException {
        String apiName = request.getParameter(METHOD);
        String json = request.getParameter(PARAMS);

        APIStore.ApiRunable apiRunable = apiStore.get(apiName);
        if (StringUtils.isEmpty(apiName)) {
            throw new ApiException("调用失败：参数'Method'为空");
        } else if (StringUtils.isEmpty(json)) {
            throw new ApiException("调用失败：参数'json'为空");
        } else if (apiRunable == null) {
            throw new ApiException("调用失败：指定api不存在,Api:" + apiName);
        }
        return apiRunable;
    }

    private <T> Object convertJsonToBean(Object val, Class<T> targetClass) throws Exception {
        Object result = null;
        if (val == null) {
            return null;
        } else if (Integer.class.equals(targetClass)) {
            result = ((Double) val).intValue();
        } else if (Long.class.equals(targetClass)) {
            result = Long.parseLong(val.toString());
        } else if (Date.class.equals(targetClass)) {
            if (val.toString().matches("[0-9]+")) {
                result = new Date(Long.parseLong(val.toString()));
            } else {
                throw new IllegalArgumentException("日期必须是长整型的时间戳");
            }
        } else if (String.class.equals(targetClass)) {
            if (val instanceof String)
                result = val;
        } else {
            result = UtilJson.mapToBean((Map<String, Object>) val, targetClass);
        }
        return result;
    }

    private Object[] buildParam(APIStore.ApiRunable apiRunable, String paramJson, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ApiException {
        Map<String, Object> map = null;
        try {
            map = UtilJson.JsonToMap(paramJson);
        } catch (IllegalArgumentException e) {
            throw new ApiException("调用失败：json字符串格式异常，请检查param参数");
        }

        if (map == null) {
            map = new HashMap<>();
        }

        Method method = apiRunable.getTargetMethod();
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<String> paramNames = Arrays.asList(parameterUtil.getParameterNames(method));

        //Arrays.asList(method.getParameterTypes());
        for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
            if (!paramNames.contains(stringObjectEntry.getKey())) {
                throw new ApiException("调用失败：接口不存在'" + stringObjectEntry.getKey() + "'参数");
            }
        }

        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i].isAssignableFrom(HttpServletRequest.class)) {
                args[i] = httpServletRequest;
            } else if (map.containsKey(paramNames.get(i))) {
                try {
                    args[i] = convertJsonToBean(map.get(paramNames.get(i)), parameterTypes[i]);
                } catch (Exception e) {
                    throw new ApiException("指定参数错误或值错误：'" + paramNames.get(i) + "'" + e.getMessage());
                }
            }
        }
        return args;
    }

    public void returnResult(Object result, HttpServletResponse httpServletResponse) {
        String json = UtilJson.ToJson(result);
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setContentType("text/html/json;charset=utf-8");
        if (!StringUtils.isEmpty(json)) {
            try {
                httpServletResponse.getWriter().write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        apiStore.loadApiFromSpringBeans();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        apiStore = new APIStore(applicationContext);
    }
}