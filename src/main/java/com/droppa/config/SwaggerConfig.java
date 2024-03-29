///**
// * 
// */
//package com.droppa.clone.droppa.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
///**
// * @author Ernest Mampana
// *
// */
//@Configuration
//@EnableSwagger2
////@Profile("dev")
//public class SwaggerConfig {
//	@Bean
//    public Docket api(){
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
//                .build()
//                .apiInfo(apiInfo());
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("KALAH GAME API")
//                .description("API FOR WEB BASED KALAH GAME")
//                .build();
//    }
//    
////    public void addResourceHandlers(ResourceHandlerRegistry registry) {
////        registry.addResourceHandler("swagger-ui.html")
////          .addResourceLocations("classpath:/META-INF/resources/");
////
////        registry.addResourceHandler("/webjars/**")
////          .addResourceLocations("classpath:/META-INF/resources/webjars/");
////    }
//}
