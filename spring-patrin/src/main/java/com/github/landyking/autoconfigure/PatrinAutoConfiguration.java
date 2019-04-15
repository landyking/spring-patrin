package com.github.landyking.autoconfigure;

import com.github.landyking.patrin.PatrinWebMvcRegistrations;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Servlet;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class})
//@ConditionalOnBean({WebMvcAutoConfiguration.class})
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class PatrinAutoConfiguration {
    @Bean
    public WebMvcRegistrations webMvcRegistrations() {
        return new PatrinWebMvcRegistrations();
    }
}
