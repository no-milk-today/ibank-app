package com.practice.drm.frontui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(
        basePackages = "com.practice.drm.clients"
)
@ConfigurationPropertiesScan
public class FrontUiApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrontUiApplication.class, args);
    }
}
