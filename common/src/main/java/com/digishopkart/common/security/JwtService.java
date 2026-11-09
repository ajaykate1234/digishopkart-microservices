package com.digishopkart.common.security;

import com.digishopkart.common.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  static final String ISSUER = "digishopkart";

  private final SecretKey key;
  private final long expirationMinutes;

  public JwtService(AppProperties properties) {
    this.key = Keys.hmacShaKeyFor(properties.jwt().secret().getBytes(StandardCharsets.UTF_8));
    this.expirationMinutes = properties.jwt().expirationMinutes();
  }

  public String generate(Long userId, String email, String role) {
    Instant now = Instant.now();
    return Jwts.builder()
        .issuer(ISSUER)
        .subject(email)
        .claim("uid", userId)
        .claim("role", role)
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
        .signWith(key)
        .compact();
  }

  public long expirationMinutes() {
    return expirationMinutes;
  }

  public Claims parse(String token) {
    return Jwts.parser().verifyWith(key).requireIssuer(ISSUER).build().parseSignedClaims(token).getPayload();
  }
}
