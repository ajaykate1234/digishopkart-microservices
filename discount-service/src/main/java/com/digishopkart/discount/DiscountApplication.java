package com.digishopkart.discount;

import com.digishopkart.common.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = "com.digishopkart")
@EnableConfigurationProperties(AppProperties.class)
public class DiscountApplication {
  public static void main(String[] args) {
    SpringApplication.run(DiscountApplication.class, args);
  }
}
