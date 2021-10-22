package com.health.job;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JobApplication {
    public static void main(String[] args) throws Exception {
        new ClassPathXmlApplicationContext("classpath:spring-jobs.xml");
        System.in.read();
    }


}
