package com.digishopkart.order.client;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignAuthForwarder {

  @Bean
  public RequestInterceptor authorizationForwarder() {
    return template -> {
      if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
        return;
      }
      HttpServletRequest request = attributes.getRequest();
      String header = request.getHeader("Authorization");
      if (header != null && !header.isBlank()) {
        template.header("Authorization", header);
      }
    };
  }
}
