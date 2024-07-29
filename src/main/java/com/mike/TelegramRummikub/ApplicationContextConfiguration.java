package com.mike.TelegramRummikub;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan
@EnableMongoRepositories(basePackages = "com.mike.TelegramRummikub.Game")
public class ApplicationContextConfiguration implements WebMvcConfigurer {
	@Bean
	public StringHttpMessageConverter stringHttpMessageConverter() {
		return new StringHttpMessageConverter(StandardCharsets.UTF_8);
	}
}