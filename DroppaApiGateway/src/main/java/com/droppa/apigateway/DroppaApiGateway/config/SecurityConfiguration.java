package com.droppa.apigateway.DroppaApiGateway.config;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import com.droppa.apigateway.DroppaApiGateway.service.JwtService;

import reactor.core.publisher.Mono;

@Configuration
public class SecurityConfiguration {
    
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            JwtService jwtService) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .addFilterAt(bearerAuthenticationFilter(jwtService), SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchange ->
                        exchange

                        .pathMatchers("/api/v1/auth/**",
                        		"/swagger-ui/**",
                        		"/webjars/**",
                        		 "/v3/api-docs/**",
                        		"/swagger-ui.html/**")
                        .permitAll()

                        .anyExchange()
                        .authenticated())

                .build();
    }

    private AuthenticationWebFilter bearerAuthenticationFilter(JwtService jwtService) {
        ReactiveAuthenticationManager authenticationManager = authentication -> {
            String token = String.valueOf(authentication.getCredentials());

            if (!jwtService.isTokenValid(token)) {
                return Mono.error(new BadCredentialsException("Invalid bearer token"));
            }

            String email = jwtService.extractUsername(token);
            if (email == null || email.isBlank()) {
                return Mono.error(new BadCredentialsException("Bearer token has no subject"));
            }

            Authentication authenticated = new UsernamePasswordAuthenticationToken(
                    email,
                    token,
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));

            return Mono.just(authenticated);
        };

        AuthenticationWebFilter authenticationFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationFilter.setServerAuthenticationConverter(exchange -> {
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Mono.empty();
            }

            String token = authHeader.substring(7);
            return Mono.just(new UsernamePasswordAuthenticationToken(null, token));
        });
        authenticationFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        return authenticationFilter;
    }
}
