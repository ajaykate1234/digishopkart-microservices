package com.digishopkart.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

  private CurrentUser() {
  }

  public static UserPrincipal require() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
      throw new IllegalStateException("Expected a signed-in account.");
    }
    return principal;
  }

  public static boolean hasRole(String role) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return false;
    }
    return authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
  }
}
