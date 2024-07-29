package com.mike.TelegramRummikub;

import et.telebof.BotClient;
import et.telebof.BotContext;
import et.telebof.types.CallbackQuery;
import et.telebof.types.Message;
import et.telebof.types.UserProfilePhotos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class TelegramBot {
	private final BotClient bot;
	@Value("${app.url}")
	private String url;
	
	private TelegramBot(@Value("${telegramBot.token}") String token) {
		bot = new BotClient(token);
		
		bot.onMessage(filter -> filter.commands("start"), this::onStartMessage);
		bot.onCallback(this::onCallbackQuery);
	}
	
	private void onCallbackQuery(BotContext context, CallbackQuery query) {
		String chatId = query.chat_instance;
		String userId = String.valueOf(query.from.id);
		String username = URLEncoder.encode(
				query.from.first_name + " " + (query.from.last_name == null ? "" : query.from.last_name), UTF_8);
		String language = query.from.language_code;
		context.answerCallbackQuery(query.id)
		       .url("%sregister?chat=%s&user=%s&username=%s&language=%s".formatted(url, chatId, userId, username,
		                                                                           language)).exec();
	}
	
	private void onStartMessage(BotContext context, Message message) {
		Locale locale = new Locale.Builder().setLanguageTag(message.from.language_code).build();
		ResourceBundle stringsBundle = ResourceBundle.getBundle("strings", locale);
		
		context.reply(stringsBundle.getString("greeting")).exec();
		context.sendGame(message.chat.id, "Rummikub").exec();
	}
	
	public void start() {
		bot.run();
	}
	
	public byte[] getUserImage(String userId) {
		UserProfilePhotos photos = bot.context.getUserProfilePhotos(Long.parseLong(userId)).exec();
		if (photos.photos.size() > 0) {
			String fileId = photos.photos.get(0).get(0).file_id;
			String filePath = bot.context.getFile(fileId).exec().file_path;
			return bot.context.downloadFile(filePath);
		}
		return null;
	}
}
