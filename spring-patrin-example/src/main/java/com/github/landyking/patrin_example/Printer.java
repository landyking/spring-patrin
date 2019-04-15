package com.github.landyking.patrin_example;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author: landy
 * @date: 2019-04-11 22:07
 */
@Service
public class Printer implements InitializingBean, ServletContextAware {
    private ServletContext servletContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        WebApplicationContext app = WebApplicationContextUtils.findWebApplicationContext(servletContext);
        RequestMappingHandlerMapping mappings = app.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mappings.getHandlerMethods();
        System.out.println("###########");
        System.out.println("###########");
        handlerMethods.forEach((k, v) -> {
            System.out.println(k.toString()+" # "+v.toString());
        });
        System.out.println("###########");
        System.out.println("###########");
    }


    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext=servletContext;
    }
}
