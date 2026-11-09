package com.digishopkart.common.security;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public final class UserPrincipal implements UserDetails {

  private final Long id;
  private final String email;
  private final String role;
  private final String passwordHash;

  public UserPrincipal(Long id, String email, String role, String passwordHash) {
    this.id = id;
    this.email = email;
    this.role = role;
    this.passwordHash = passwordHash;
  }

  public static UserPrincipal fromToken(Long id, String email, String role) {
    return new UserPrincipal(id, email, role, "");
  }

  public Long getId() {
    return id;
  }

  public String getRole() {
    return role;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role));
  }

  @Override
  public String getPassword() {
    return passwordHash;
  }

  @Override
  public String getUsername() {
    return email;
  }
}
