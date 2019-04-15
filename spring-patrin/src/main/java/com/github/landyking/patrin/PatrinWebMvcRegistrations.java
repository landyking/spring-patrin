package com.github.landyking.patrin;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class PatrinWebMvcRegistrations implements WebMvcRegistrations {
    private static class RequestMappingLazyHolder {
        static RequestMappingHandlerMapping INSTANCE = new PatrinRequestMappingHandlerMapping();
    }

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return RequestMappingLazyHolder.INSTANCE;
    }
}
