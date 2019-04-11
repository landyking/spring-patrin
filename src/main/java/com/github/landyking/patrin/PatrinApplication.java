package com.github.landyking.patrin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"controller","com.github.landyking"})
public class PatrinApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatrinApplication.class, args);
    }

}
