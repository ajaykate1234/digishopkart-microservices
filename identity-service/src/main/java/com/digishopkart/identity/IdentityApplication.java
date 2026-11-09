package com.digishopkart.identity;

import com.digishopkart.common.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.digishopkart")
@EnableConfigurationProperties(AppProperties.class)
@EnableFeignClients
public class IdentityApplication {
  public static void main(String[] args) {
    SpringApplication.run(IdentityApplication.class, args);
  }
}
