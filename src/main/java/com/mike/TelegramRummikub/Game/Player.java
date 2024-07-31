package com.mike.TelegramRummikub.Game;

import java.util.List;

public class Player extends CommonPlayer {
	private String username;
	private List<List<Tile>> tiles;
	private boolean isFirstMoveMade;
	private boolean isConnected;
	
	public Player(String userId, List<List<Tile>> tiles, boolean isFirstMoveMade, boolean isConnected, String image) {
		this.isFirstMoveMade = isFirstMoveMade;
		this.isConnected = isConnected;
		this.userId = userId;
		this.tiles = tiles;
		this.image = image;
	}
	
	public Player() {
	}
	
	public Player(String userId, String username, String image) {
		this.isFirstMoveMade = false;
		this.userId = userId;
		this.username = username;
		isConnected = true;
		this.image = image;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public List<List<Tile>> getTiles() {
		return tiles;
	}
	
	public void setTiles(List<List<Tile>> tiles) {
		this.tiles = tiles;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public boolean isFirstMoveMade() {
		return isFirstMoveMade;
	}
	
	public void setFirstMoveMade(boolean firstMoveMade) {
		isFirstMoveMade = firstMoveMade;
	}
	
	public void addTile(Tile tile) {
		for (List<Tile> tileList : tiles) {
			for (int j = 0; j < tileList.size(); j++) {
				if (tileList.get(j) == null) {
					tileList.set(j, tile);
					return;
				}
			}
		}
	}
	
	public int calculateScore() {
		int score = 0;
		for (List<Tile> tileRow : tiles) {
			for (Tile t : tileRow) {
				if (t == null) continue;
				switch (t.getNumber()) {
					case ONE -> score += 1;
					case TWO -> score += 2;
					case THREE -> score += 3;
					case FOUR -> score += 4;
					case FIVE -> score += 5;
					case SIX -> score += 6;
					case SEVEN -> score += 7;
					case EIGHT -> score += 8;
					case NINE -> score += 9;
					case TEN -> score += 10;
					case ELEVEN -> score += 11;
					case TWELVE -> score += 12;
					case THIRTEEN -> score += 13;
					case JOKER -> score += 30;
				}
			}
		}
		return score;
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
	public void setConnected(boolean connected) {
		isConnected = connected;
	}
}
