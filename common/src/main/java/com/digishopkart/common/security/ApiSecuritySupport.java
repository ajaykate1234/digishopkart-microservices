package com.digishopkart.common.security;

import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class ApiSecuritySupport {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public ApiSecuritySupport(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  public SecurityFilterChain build(
      HttpSecurity http,
      Consumer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> rules
  ) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(rules::accept)
        .exceptionHandling(errors -> errors
            .authenticationEntryPoint((request, response, exception) ->
                write(response, 401, "Unauthorized", "Sign in to continue."))
            .accessDeniedHandler((request, response, exception) ->
                write(response, 403, "Forbidden", "Your role cannot access this action.")))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  private static void write(HttpServletResponse response, int status, String error, String message) throws java.io.IOException {
    response.setStatus(status);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(
        "{\"status\":" + status + ",\"error\":\"" + error + "\",\"message\":\"" + message + "\",\"fields\":[]}");
  }
}
