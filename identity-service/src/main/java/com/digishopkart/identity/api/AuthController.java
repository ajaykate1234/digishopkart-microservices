package com.digishopkart.identity.api;

import com.digishopkart.common.security.CurrentUser;
import com.digishopkart.identity.api.AuthDtos.AuthResponse;
import com.digishopkart.identity.api.AuthDtos.LoginRequest;
import com.digishopkart.identity.api.AuthDtos.MeResponse;
import com.digishopkart.identity.api.AuthDtos.RegisterRequest;
import com.digishopkart.identity.service.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/api/v1/auth/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest request) {
    return authService.login(request);
  }

  @PostMapping("/api/v1/auth/register")
  public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
    return authService.register(request);
  }

  @GetMapping("/api/v1/auth/me")
  public MeResponse me() {
    return authService.me(CurrentUser.require());
  }

  @GetMapping("/api/v1/health")
  public Map<String, String> health() {
    return Map.of("status", "UP", "application", "identity-service");
  }
}
