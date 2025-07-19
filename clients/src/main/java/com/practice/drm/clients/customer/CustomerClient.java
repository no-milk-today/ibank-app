package com.practice.drm.clients.customer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("customer")
public interface CustomerClient {

    @GetMapping("/api/v1/customers/main")
    MainPageData getMainPage(@RequestParam("login") String login);
}
