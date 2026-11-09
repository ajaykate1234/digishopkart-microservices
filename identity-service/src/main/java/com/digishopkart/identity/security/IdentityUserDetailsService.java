package com.digishopkart.identity.security;

import com.digishopkart.common.security.UserPrincipal;
import com.digishopkart.identity.domain.UserEntity;
import com.digishopkart.identity.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class IdentityUserDetailsService implements UserDetailsService {

  private final UserRepository users;

  public IdentityUserDetailsService(UserRepository users) {
    this.users = users;
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    UserEntity user = users.findByEmailIgnoreCase(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));
    if (!user.isEnabled()) {
      throw new org.springframework.security.authentication.DisabledException("blocked");
    }
    return new UserPrincipal(user.getId(), user.getEmail(), user.getRole().getCode(), user.getPasswordHash());
  }
}
