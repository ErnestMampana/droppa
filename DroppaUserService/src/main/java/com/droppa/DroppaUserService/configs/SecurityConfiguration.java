package com.droppa.DroppaUserService.configs;

import com.droppa.DroppaUserService.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;
  
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
          .csrf(csrf -> csrf.disable())
          .httpBasic(httpBasic -> httpBasic.disable())
          .formLogin(formLogin -> formLogin.disable())
          .logout(logout -> logout.disable())
          .authorizeHttpRequests(auth -> auth
              .requestMatchers(
                  "/api/v1/auth/**",
                  "/api/v1/user/getuserbyemail/**",
                  "/api/v1/user/accounts/**",
                  "/swagger-ui/**",
                  "/v3/api-docs/**",
                  "/error"
              ).permitAll()
              .anyRequest().authenticated()
          )
          .sessionManagement(session -> session
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )
          .exceptionHandling(exception -> exception
              .authenticationEntryPoint((request, response, authException) ->
                  response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
              )
              .accessDeniedHandler((request, response, accessDeniedException) ->
                  response.sendError(HttpServletResponse.SC_FORBIDDEN)
              )
          )
          .authenticationProvider(authenticationProvider)
          .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

      return http.build();
  }
}
