package com.plantswap.listings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ListingsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ListingsServiceApplication.class, args);
    }
}
