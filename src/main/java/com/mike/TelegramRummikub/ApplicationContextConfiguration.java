package com.mike.TelegramRummikub;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan
@EnableMongoRepositories(basePackages = "com.mike.TelegramRummikub.Game")
public class ApplicationContextConfiguration {
}