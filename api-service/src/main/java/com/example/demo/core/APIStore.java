package com.example.demo.core;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by wangyonghua on 2017/10/28.
 */
public class APIStore {
    private ApplicationContext applicationContext;
    private HashMap<String, ApiRunable> apiMap = new HashMap<>();

    public APIStore(ApplicationContext applicationContext) {
        Assert.notNull(applicationContext);
        this.applicationContext = applicationContext;
    }

    public void loadApiFromSpringBeans() {
        //ioc 所有bean
        String[] names = applicationContext.getBeanDefinitionNames();
        Class<?> type;
        for (String name : names) {
            if (!name.equals("goodService")) continue;
            type = applicationContext.getType(name);

            for (Method method : type.getDeclaredMethods()) {
                APIMapper apiMapper = method.getAnnotation(APIMapper.class);
                if (apiMapper != null) {
                    addApiItem(apiMapper, name, method);
                }
            }
        }
    }

    public ApiRunable get(String apiName) {
        return apiMap.get(apiName);
    }

    private void addApiItem(APIMapper apiMapper, String name, Method method) {
        ApiRunable apiRunable = new ApiRunable();
        apiRunable.setApiName(apiMapper.value());
        apiRunable.setTargetMethod(method);
        apiRunable.setTargetName(name);
        apiMap.put(apiMapper.value(), apiRunable);
    }

    public class ApiRunable {
        private String apiName;
        private String targetName;
        private Method targetMethod;
        private Object target;

        public String getApiName() {
            return apiName;
        }

        public void setApiName(String apiName) {
            this.apiName = apiName;
        }

        public String getTargetName() {
            return targetName;
        }

        public void setTargetName(String targetName) {
            this.targetName = targetName;
        }

        public Method getTargetMethod() {
            return targetMethod;
        }

        public void setTargetMethod(Method targetMethod) {
            this.targetMethod = targetMethod;
        }

        public Object run(Object... args) throws InvocationTargetException, IllegalAccessException {
            if (target == null) {
                target = applicationContext.getBean(targetName);
            }
            return targetMethod.invoke(target, args);
        }
    }

}
