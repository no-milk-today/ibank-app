package com.practice.drm.clients.customer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("customer")
public interface CustomerClient {

    @GetMapping("/api/v1/customers/main")
    MainPageData getMainPage(@RequestParam("login") String login);

    @PostMapping("/api/v1/customers/user/{login}/editPassword")
    List<String> editPassword(
            @PathVariable("login") String login,
            @RequestBody EditPasswordRequest request
    );
}
