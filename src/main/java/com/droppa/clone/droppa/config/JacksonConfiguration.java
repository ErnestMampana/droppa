///**
// *
// */
//package com.droppa.clone.droppa.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.MapperFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
///**
// * @author Ernest Mampana
// *
// */
//@Configuration
//public class JacksonConfiguration {
//	@SuppressWarnings("deprecation")
//	@Bean
//	public ObjectMapper objectMapper() {
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
//
//		return mapper;
//	}
//}
