package com.droppa.apigateway.DroppaApiGateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.droppa.apigateway.DroppaApiGateway.service.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter {

    private static final String USER_EMAIL_HEADER = "X-User-Email";

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {


        String authHeader =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst(HttpHeaders.AUTHORIZATION);

        // Never trust identity headers supplied by clients.
        ServerHttpRequest.Builder requestBuilder =
                exchange.getRequest()
                        .mutate()
                        .headers(headers -> headers.remove(USER_EMAIL_HEADER));

        //No token just pass through
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(
                    exchange.mutate()
                            .request(requestBuilder.build())
                            .build()
            );
        }
        

        String token = authHeader.substring(7);

        try {

            // Validate token
            if (!jwtService.isTokenValid(token)) {

                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Extract email safely
            String email = jwtService.extractUsername(token);

            if (email == null || email.isBlank()) {
                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Inject identity into downstream request
            ServerHttpRequest mutatedRequest =
                    requestBuilder
                            .headers(headers -> headers.set(USER_EMAIL_HEADER, email))
                            .build();

            return chain.filter(
                    exchange.mutate()
                            .request(mutatedRequest)
                            .build()
            );

        } catch (Exception e) {
            // Any JWT parsing error reject request
            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
