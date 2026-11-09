package com.digishopkart.customer;

import com.digishopkart.common.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = "com.digishopkart")
@EnableConfigurationProperties(AppProperties.class)
public class CustomerApplication {
  public static void main(String[] args) {
    SpringApplication.run(CustomerApplication.class, args);
  }
}
