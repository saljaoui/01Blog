package com._blog.backend.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
@RestController
public class EndpointsLogger implements CommandLineRunner {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Override
    public void run(String... args) throws Exception {
        handlerMapping.getHandlerMethods()
                .forEach((key, value) ->
                        System.out.println(key + " -> " + value));
    }
}
