package com.practice.drm.exchangegenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableDiscoveryClient
@EnableFeignClients(
        basePackages = "com.practice.drm.clients"
)
@EnableScheduling
@PropertySources({
        @PropertySource("classpath:clients-${spring.profiles.active}.properties")
})
public class ExchangeGeneratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExchangeGeneratorApplication.class, args);
    }
}
