package com.droppa.apigateway.DroppaApiGateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class JwtService {

  private final String secretKey;

  public JwtService(@Value("${security.jwt.secret}") String secretKey) {
    this.secretKey = secretKey;
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }
  
  public boolean isTokenValid(String token) {
	    try {
	        Jwts.parserBuilder()
	            .setSigningKey(getSignInKey())
	            .build()
	            .parseClaimsJws(token); // THIS is the real validation

	        return true;
	    } catch (Exception e) {
	    	
	    	    log.error("Token validation failed", e);
	    	    return false;
	    	
	    }
	}

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSignInKey() {
	    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
	    return Keys.hmacShaKeyFor(keyBytes);
	}
}
