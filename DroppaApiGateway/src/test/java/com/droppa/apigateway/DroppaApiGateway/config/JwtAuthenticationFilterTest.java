package com.droppa.apigateway.DroppaApiGateway.config;

import com.droppa.apigateway.DroppaApiGateway.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private static final String USER_EMAIL_HEADER = "X-User-Email";

    @Test
    void validTokenReplacesClientSuppliedUserEmailHeader() {
        JwtService jwtService = mock(JwtService.class);
        when(jwtService.isTokenValid("token")).thenReturn(true);
        when(jwtService.extractUsername("token")).thenReturn("driver@example.com");

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/v1/drivers/createdriver")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .header(USER_EMAIL_HEADER, "spoof@example.com")
        );
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        GatewayFilterChain chain = forwardedExchange -> {
            chainCalled.set(true);
            assertEquals("driver@example.com", forwardedExchange.getRequest()
                    .getHeaders()
                    .getFirst(USER_EMAIL_HEADER));
            assertEquals(1, forwardedExchange.getRequest()
                    .getHeaders()
                    .get(USER_EMAIL_HEADER)
                    .size());
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        assertTrue(chainCalled.get());
    }

    @Test
    void requestWithoutTokenDoesNotForwardClientSuppliedUserEmailHeader() {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(mock(JwtService.class));
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/auth/login")
                        .header(USER_EMAIL_HEADER, "spoof@example.com")
        );
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        GatewayFilterChain chain = forwardedExchange -> {
            chainCalled.set(true);
            assertFalse(forwardedExchange.getRequest()
                    .getHeaders()
                    .containsKey(USER_EMAIL_HEADER));
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        assertTrue(chainCalled.get());
    }
}
