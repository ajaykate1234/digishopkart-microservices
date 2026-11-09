package com.digishopkart.identity.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "customer-service", url = "${digishopkart.customer-url}")
public interface CustomerClient {

  @PostMapping("/api/v1/customers")
  void create(@RequestHeader("Authorization") String authorization, @RequestBody CustomerCreate body);

  record CustomerCreate(String firstName, String lastName, String email, String mobile, String customerStatus) {
  }
}
