package com.github.landyking.patrin;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

import static org.junit.Assert.*;

public class PatrinRequestMappingHandlerMappingTest {
    @Test
    public void test1() {
        System.out.println("11".substring(2));
    }

    @Test
    public void test2() {
        Map.Entry<String, RequestMethod> et = new PatrinRequestMappingHandlerMapping().processMethodName("GET__he_llo__$world$__test");
        String name = et.getKey();
        assertEquals("/he_llo/{world}/test", name);
        assertEquals(RequestMethod.GET, et.getValue());
    }

    @Test
    public void test22() {
        Map.Entry<String, RequestMethod> et = new PatrinRequestMappingHandlerMapping().processMethodName("$world$__test");
        String name = et.getKey();
        assertEquals("/{world}/test", name);
        assertNull(et.getValue());

        name = new PatrinRequestMappingHandlerMapping().processMethodName("$world$").getKey();
        assertEquals("/{world}", name);

        et = new PatrinRequestMappingHandlerMapping().processMethodName("DELETE__$world$");
        name = et.getKey();
        assertEquals("/{world}", name);
        assertEquals(RequestMethod.DELETE, et.getValue());
    }

    @Test
    public void test33() {
        String name = new PatrinRequestMappingHandlerMapping().processHandlerName("controller.api.v1.private_.$merchant$.limit.__Controller");
        assertEquals("api/v1/private/{merchant}/limit", name);

        name = new PatrinRequestMappingHandlerMapping().processHandlerName("controller.api.v1.private_.$merchant$.limit.HelloController");
        assertEquals("api/v1/private/{merchant}/limit/hello", name);

        name = new PatrinRequestMappingHandlerMapping().processHandlerName("controller.api.v1.private_.$merchant$.limit.Hello_WorldController");
        assertEquals("api/v1/private/{merchant}/limit/hello_world", name);

        name = new PatrinRequestMappingHandlerMapping().processHandlerName("controller.Hello_WorldController");
        assertEquals("hello_world", name);
    }
}