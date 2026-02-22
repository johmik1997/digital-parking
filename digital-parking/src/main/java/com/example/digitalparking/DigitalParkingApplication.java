package com.example.digitalparking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class DigitalParkingApplication {

    public static void main(String[] args) {
        // Start Spring Boot and get context
        ConfigurableApplicationContext context = SpringApplication.run(DigitalParkingApplication.class, args);

        // Get Environment from context
        Environment environment = context.getEnvironment();

        // Print external URL and other startup traces
        System.out.println(ApplicationStartupTraces.of(environment));
    }
}
