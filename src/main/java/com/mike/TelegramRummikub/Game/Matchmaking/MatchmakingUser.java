package com.mike.TelegramRummikub.Game.Matchmaking;


import com.mike.TelegramRummikub.Game.CommonPlayer;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "matchmaking")
public class MatchmakingUser extends CommonPlayer {
	private String gameId;
	private String username;
	@Indexed(name = "ttl_index", expireAfterSeconds = 14400)//4 hours
	private Date createdAt;
	
	public MatchmakingUser(String gameId, String userId, String username, Date createdAt) {
		this.gameId = gameId;
		this.createdAt = createdAt;
		this.userId = userId;
		this.username = username;
	}
	
	public MatchmakingUser() {
	}
	
	public MatchmakingUser(String gameId, String userId, String username) {
		this.gameId = gameId;
		this.userId = userId;
		this.username = username;
		createdAt = new Date();
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
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}