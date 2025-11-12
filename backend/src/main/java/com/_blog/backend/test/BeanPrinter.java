package com._blog.backend.test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class BeanPrinter implements CommandLineRunner {

    private final ApplicationContext context;

    public BeanPrinter(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(String... args) throws Exception {
        String[] beanNames = context.getBeanDefinitionNames();
        Arrays.sort(beanNames); // optional, just to sort
        for (String name : beanNames) {
            System.out.println(name);
        }
    }
}
