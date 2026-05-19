/**
 *
 */
package com.droppa.DroppaBookingService.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Ernest Mampana
 *
 */
@Configuration
public class ModelMapperConfig {
	
	@Bean
	public ModelMapper modelMapper() {

		return new ModelMapper();
	}
}
