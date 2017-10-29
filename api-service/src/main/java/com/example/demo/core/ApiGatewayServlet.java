package com.example.demo.core;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by wangyonghua on 2017/10/28.
 */
@WebServlet(urlPatterns = "/api/*", description = "网关组件")
public class ApiGatewayServlet extends HttpServlet {
    ApplicationContext applicationContext;
    private ApiGatewayHand apiGatewayHand;

    @Override
    public void init() throws ServletException {
        super.init();
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        apiGatewayHand = applicationContext.getBean(ApiGatewayHand.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            apiGatewayHand.handle(req, resp);
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            apiGatewayHand.handle(req, resp);
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
