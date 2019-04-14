package com.github.landyking.patrin;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class PatrinRequestMappingHandlerMappingTest {
    @Test
    public void test1() {
        System.out.println("11".substring(2));
    }

    @Test
    public void test2() {
        String name = new PatrinRequestMappingHandlerMapping().processMethodName("GET__he_llo__$world$__test");
        Assert.assertEquals("/he_llo/{world}/test",name);
    }

    @Test
    public void test22() {
        String name = new PatrinRequestMappingHandlerMapping().processMethodName("$world$__test");
        Assert.assertEquals("/{world}/test",name);

        name = new PatrinRequestMappingHandlerMapping().processMethodName("$world$");
        Assert.assertEquals("/{world}",name);

        name = new PatrinRequestMappingHandlerMapping().processMethodName("DELETE__$world$");
        Assert.assertEquals("/{world}",name);
    }

    @Test
    public void test33() {
        String name = new PatrinRequestMappingHandlerMapping().processHandlerName("controller.api.v1.private_.$merchant$.limit.__Controller");
        Assert.assertEquals("/api/v1/private/{merchant}/limit",name);

        name = new PatrinRequestMappingHandlerMapping().processHandlerName("controller.api.v1.private_.$merchant$.limit.HelloController");
        Assert.assertEquals("/api/v1/private/{merchant}/limit/hello",name);

        name = new PatrinRequestMappingHandlerMapping().processHandlerName("controller.api.v1.private_.$merchant$.limit.Hello_WorldController");
        Assert.assertEquals("/api/v1/private/{merchant}/limit/hello_world",name);
    }
}