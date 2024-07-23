package com.mike.TelegramRummikub.Game.Matchmaking;


import com.mike.TelegramRummikub.TelegramRummikubApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Document(collection = "matchmaking")
public class MatchmakingUser {
	@Id
	private String userId;
	private String gameId;
	private String username;
	
	@Transient
	private final String tempDir = "/tmp/";
	
	public MatchmakingUser(String gameId, String userId, String username) {
		this.gameId = gameId;
		this.userId = userId;
		this.username = username;
	}
	
	public MatchmakingUser() {
	}
	
	public String getGameId() {
		return gameId;
	}
	
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getImage() throws IOException {
		byte[] image = TelegramRummikubApplication.getTelegramBot().getUserImage(userId);
		if(image == null){
			return null;
		}
		String filename = tempDir + userId + ".jpg";
		new File(filename).createNewFile();
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(filename);
			outputStream.write(image, 0, image.length);
			outputStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return filename;
	}
	
	public String getUsername() {
		return username.replace('%', ' ');
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void deleteImg() {
		File f = new File(tempDir + userId + ".jpg");
		if(f.exists()) f.delete();
	}
}