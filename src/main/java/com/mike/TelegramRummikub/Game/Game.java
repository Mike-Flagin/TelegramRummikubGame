package com.mike.TelegramRummikub.Game;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

import static com.mike.TelegramRummikub.Game.GameController.PlayerData;
import static com.mike.TelegramRummikub.Game.GameController.ScoreUser;

@Document(collection = "games")
public class Game {
	@Id
	private String gameId;
	private List<Player> players;
	private String currentPlayerId;
	private Table table;
	private List<Tile> tiles;
	private boolean win;
	@Indexed(name = "ttl_index", expireAfterSeconds = 14400)//4 hours
	private Date createdAt;
	
	public List<Tile> getTiles() {
		return tiles;
	}
	
	public void setTiles(List<Tile> tiles) {
		this.tiles = tiles;
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	
	
	public Game(String gameId, List<Player> players, String currentPlayerId, Table table, List<Tile> tiles, boolean win,
	            Date createdAt) {
		this.gameId = gameId;
		this.players = players;
		this.currentPlayerId = currentPlayerId;
		this.table = table;
		this.tiles = tiles;
		this.win = win;
		this.createdAt = createdAt;
	}
	
	public Game(String gameId, List<Player> players) {
		this.gameId = gameId;
		this.players = players;
		this.win = false;
		this.tiles = new ArrayList<>();
		this.createdAt = new Date();
	}
	
	public Game() {}
	
	public void startGame() {
		//create tiles
		for (Color color : Color.values()) {
			for (Number number : Number.values()) {
				if (number == Number.JOKER && (color != Color.BLACK && color != Color.RED)) continue;
				if (number == Number.JOKER) {
					tiles.add(new Tile(color, number));
				} else {
					tiles.add(new Tile(color, number));
					tiles.add(new Tile(color, number));
				}
			}
		}
		//players get tiles
		Random random = new Random();
		for (Player player : players) {
			List<List<Tile>> playerTiles = new ArrayList<>();
			List<Tile> p = new ArrayList<>();
			for (int i = 0; i < 14; i++) {
				int t = random.nextInt(0, tiles.size());
				p.add(tiles.get(t));
				tiles.remove(t);
			}
			playerTiles.add(p);
			player.setTiles(playerTiles);
		}
		
		//set first move
		currentPlayerId = players.get(random.nextInt(0, players.size())).getUserId();
	}
	
	public String getGameId() {
		return gameId;
	}
	
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	
	public String getCurrentPlayerId() {
		return currentPlayerId;
	}
	
	public void setCurrentPlayerId(String currentPlayerId) {
		this.currentPlayerId = currentPlayerId;
	}
	
	public Table getTable() {
		return table;
	}
	
	public void setTable(Table table) {
		this.table = table;
	}
	
	/**
	 * @param userId userId
	 * @param table  table object
	 * @param tiles  user tiles
	 * @return true if board changed or player take tile, false otherwise
	 */
	public boolean makeMove(String userId, Table table, List<List<Tile>> tiles) {
		//get player
		Player player = players.stream().filter((user) -> user.userId.equals(userId)).findFirst().orElseThrow();
		
		//is move correct
		if (userId.equals(currentPlayerId)) {
			//count tiles of player and player tiles sent
			int tilesCount = 0, playerTilesCount = 0;
			for (List<Tile> row : tiles)
				for (Tile t : row)
					if (t != null) tilesCount++;
			for (List<Tile> row : player.getTiles())
				for (Tile t : row)
					if (t != null) playerTilesCount++;
			
			//user not moved
			if (tilesCount == playerTilesCount) {
				player.setTiles(tiles);
				giveTileUser(player);
				setNextPlayer();
				return true;
			} else {
				if (!player.isFirstMoveMade()) {
					List<Tile> newTileList = tiles.stream().flatMap(List::stream).toList();
					List<Tile> oldTileList = new ArrayList<>(player.getTiles().stream().flatMap(List::stream).toList());
					for (Tile t : newTileList) {
						oldTileList.remove(t);
					}
					
					if (oldTileList.stream().anyMatch((tile) -> tile.getNumber() == Number.JOKER)) return false;
					int sum = 0;
					for (Tile t : oldTileList) {
						switch (t.getNumber()) {
							case ONE -> sum += 1;
							case TWO -> sum += 2;
							case THREE -> sum += 3;
							case FOUR -> sum += 4;
							case FIVE -> sum += 5;
							case SIX -> sum += 6;
							case SEVEN -> sum += 7;
							case EIGHT -> sum += 8;
							case NINE -> sum += 9;
							case TEN -> sum += 10;
							case ELEVEN -> sum += 11;
							case TWELVE -> sum += 12;
							case THIRTEEN -> sum += 13;
						}
					}
					
					if (sum >= 30 && table.checkRules()) {
						player.setFirstMoveMade(true);
						this.table = table;
						setNextPlayer();
						player.setTiles(tiles);
						return true;
					} else {
						return false;
					}
				}
				if (table.checkRules()) {
					this.table = table;
					if (tilesCount == 0) {
						win = true;
					} else {
						setNextPlayer();
					}
					player.setTiles(tiles);
					return true;
				} else {
					return false;
				}
			}
		} else {//not current user
			return false;
		}
	}
	
	private void setNextPlayer() {
		List<String> ids = new ArrayList<>();
		players.forEach((user) -> ids.add(user.getUserId()));
		int currentIdIndex = ids.indexOf(currentPlayerId);
		if (currentIdIndex == ids.size() - 1) {
			currentIdIndex = 0;
		} else {
			currentIdIndex++;
		}
		currentPlayerId = ids.get(currentIdIndex);
	}
	
	private void giveTileUser(Player player) {
		Random rand = new Random();
		int randomTile = rand.nextInt(0, this.tiles.size());
		player.addTile(this.tiles.get(randomTile));
		this.tiles.remove(randomTile);
	}
	
	public void updateBoard(String userId, List<List<Tile>> tiles) {
		Player player = players.stream().filter((user) -> user.userId.equals(userId)).findFirst().orElseThrow();
		player.setTiles(tiles);
	}
	
	public boolean isWin() {
		return win;
	}
	
	public void setWin(boolean win) {
		this.win = win;
	}
	
	public List<ScoreUser> calculateScore() {
		List<ScoreUser> res = new ArrayList<>();
		players.forEach((p) -> res.add(
				new ScoreUser(new PlayerData(p.getUserId(), p.getUsername(), p.getImage()), p.calculateScore())));
		res.sort(Comparator.comparingInt(ScoreUser::score));
		return res;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}