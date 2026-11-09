package com.digishopkart.identity.security;

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
        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/register").permitAll()
        .requestMatchers("/api/v1/health").permitAll()
        .anyRequest().authenticated());
  }
}
