package com.mike.TelegramRummikub.Game;

import com.mike.TelegramRummikub.Game.Matchmaking.MatchmakingUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.mike.TelegramRummikub.Game.GameController.*;

@Service
public class GameService {
	@Autowired
	private GameRepository repository;
	
	public void createGame(String gameId, List<MatchmakingUser> users) {
		List<Player> players = new ArrayList<>();
		users.forEach((user) -> players.add(new Player(user.getUserId(), user.getUsername())));
		Game game = new Game(gameId, players);
		game.startGame();
		repository.save(game);
	}
	
	
	public GameData getGameForUser(String gameId, String userId) {
		Game game = repository.findById(gameId).orElseThrow();
		List<PlayerData> players = new ArrayList<>();
		game.getPlayers().forEach(
				player -> players.add(new PlayerData(player.getUserId(), player.getUsername(), player.getImage())));
		return new GameData(players, game.getCurrentPlayerId(), game.getTable(),
		                    game.getPlayers().stream().filter(player -> player.userId.equals(userId)).findFirst()
		                        .orElseThrow().getTiles());
	}
	
	/**
	 * @param update update
	 * @return true if move made, false otherwise
	 */
	public boolean handleUpdate(GameUpdate update) {
		Game game = repository.findById(update.user().gameId()).orElseThrow();
		boolean res = game.makeMove(update.user().userId(), update.table(), update.tiles());
		repository.save(game);
		return res;
	}
	
	public void saveBoard(GameUpdate update) {
		Game game = repository.findById(update.user().gameId()).orElseThrow();
		game.updateBoard(update.user().userId(), update.tiles());
		repository.save(game);
	}
	
	public Game getGameById(String gameId) {
		return repository.findById(gameId).orElseThrow();
	}
	
	public List<ScoreUser> getUserScoresByGameId(String gameId) {
		Game game = repository.findById(gameId).orElseThrow();
		return game.calculateScore();
	}
}
