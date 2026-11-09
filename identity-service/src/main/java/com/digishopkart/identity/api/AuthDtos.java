package com.digishopkart.identity.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
  private AuthDtos() {
  }

  public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {
  }

  public record RegisterRequest(
      @NotBlank @Size(max = 60) String firstName,
      @NotBlank @Size(max = 60) String lastName,
      @Email @NotBlank String email,
      @NotBlank @Size(max = 15) String mobile,
      @NotBlank @Size(min = 8, max = 72) String password
  ) {
  }

  public record MeResponse(Long id, String email, String fullName, String role) {
  }

  public record AuthResponse(String tokenType, String accessToken, long expiresInMinutes, MeResponse user) {
  }
}
