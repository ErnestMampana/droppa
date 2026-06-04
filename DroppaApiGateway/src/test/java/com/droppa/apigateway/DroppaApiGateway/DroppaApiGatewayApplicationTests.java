package com.droppa.apigateway.DroppaApiGateway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.core.env.Environment;

import java.time.Duration;
import java.util.List;

@SpringBootTest
class DroppaApiGatewayApplicationTests {

	@Autowired
	private RouteDefinitionLocator routeDefinitionLocator;

	@Autowired
	private Environment environment;

	@Test
	void contextLoads() {
	}

	@Test
	void exposesSwaggerDocsForDownstreamServices() {
		List<String> routeIds = routeDefinitionLocator.getRouteDefinitions()
				.map(RouteDefinition::getId)
				.collectList()
				.block(Duration.ofSeconds(5));

		Assertions.assertNotNull(routeIds);
		Assertions.assertTrue(routeIds.contains("user-service-api-docs"));
		Assertions.assertTrue(routeIds.contains("booking-service-api-docs"));
		Assertions.assertTrue(routeIds.contains("driver-service-api-docs"));
		Assertions.assertEquals("/v3/api-docs/user-service", environment.getProperty("springdoc.swagger-ui.urls[0].url"));
		Assertions.assertEquals("/v3/api-docs/booking-service", environment.getProperty("springdoc.swagger-ui.urls[1].url"));
		Assertions.assertEquals("/v3/api-docs/driver-service", environment.getProperty("springdoc.swagger-ui.urls[2].url"));
	}

}
