package com.mike.TelegramRummikub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class TelegramRummikubApplication {
	private static TelegramBot telegramBot;
	
	public static void main(String[] args) {
		SpringApplication.run(TelegramRummikubApplication.class, args);
		ApplicationContext ctx = new AnnotationConfigApplicationContext(ApplicationContextConfiguration.class);
		
		telegramBot = ctx.getBean(TelegramBot.class);
		telegramBot.start();
	}
	
	public static TelegramBot getTelegramBot() {
		return telegramBot;
	}
}
