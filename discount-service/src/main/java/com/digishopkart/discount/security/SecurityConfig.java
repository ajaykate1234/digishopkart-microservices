package com.digishopkart.discount.security;

import com.digishopkart.common.security.ApiSecuritySupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  private final ApiSecuritySupport support;

  public SecurityConfig(ApiSecuritySupport support) {
    this.support = support;
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return support.build(http, auth -> auth
        .requestMatchers("/api/v1/health").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/v1/discounts", "/api/v1/discounts/**").permitAll()
        .requestMatchers("/api/v1/discounts", "/api/v1/discounts/**").hasRole("ADMIN")
        .anyRequest().authenticated());
  }
}
