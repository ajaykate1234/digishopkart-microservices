package com.digishopkart.identity.service;

import com.digishopkart.common.exception.ConflictException;
import com.digishopkart.common.security.JwtService;
import com.digishopkart.common.security.UserPrincipal;
import com.digishopkart.identity.api.AuthDtos.AuthResponse;
import com.digishopkart.identity.api.AuthDtos.LoginRequest;
import com.digishopkart.identity.api.AuthDtos.MeResponse;
import com.digishopkart.identity.api.AuthDtos.RegisterRequest;
import com.digishopkart.identity.client.CustomerClient;
import com.digishopkart.identity.client.CustomerClient.CustomerCreate;
import com.digishopkart.identity.domain.RoleEntity;
import com.digishopkart.identity.domain.UserEntity;
import com.digishopkart.identity.repo.RoleRepository;
import com.digishopkart.identity.repo.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final UserRepository users;
  private final RoleRepository roles;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final CustomerClient customerClient;

  public AuthService(
      UserRepository users,
      RoleRepository roles,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwtService jwtService,
      CustomerClient customerClient
  ) {
    this.users = users;
    this.roles = roles;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.customerClient = customerClient;
  }

  public AuthResponse login(LoginRequest request) {
    var authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email().trim(), request.password()));
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    UserEntity user = users.findByEmailIgnoreCase(principal.getUsername()).orElseThrow();
    return token(user);
  }

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    String email = request.email().trim().toLowerCase();
    if (users.existsByEmailIgnoreCase(email)) {
      throw new ConflictException("An account with this email already exists.");
    }
    RoleEntity role = roles.findByCode("CUSTOMER").orElseThrow();
    UserEntity user = new UserEntity();
    user.setEmail(email);
    user.setFullName((request.firstName().trim() + " " + request.lastName().trim()).trim());
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    user.setRole(role);
    user = users.saveAndFlush(user);

    String bearer = "Bearer " + jwtService.generate(user.getId(), user.getEmail(), user.getRole().getCode());
    customerClient.create(bearer, new CustomerCreate(
        request.firstName().trim(),
        request.lastName().trim(),
        email,
        request.mobile().trim(),
        "active"));
    return token(user);
  }

  public MeResponse me(UserPrincipal principal) {
    UserEntity user = users.findByEmailIgnoreCase(principal.getUsername()).orElseThrow();
    return new MeResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole().getCode());
  }

  private AuthResponse token(UserEntity user) {
    return new AuthResponse(
        "Bearer",
        jwtService.generate(user.getId(), user.getEmail(), user.getRole().getCode()),
        jwtService.expirationMinutes(),
        new MeResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole().getCode()));
  }
}
