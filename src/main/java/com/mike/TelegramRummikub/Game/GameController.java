package com.mike.TelegramRummikub.Game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GameController {
	@Autowired
	private GameService gameService;
	private final SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	public GameController(SimpMessagingTemplate messagingTemplate) {this.messagingTemplate = messagingTemplate;}
	
	@GetMapping("/game")
	public String startGame(@RequestParam("gameId") String gameId, @RequestParam("userId") String userId) {
		return "game";
	}
	
	@MessageMapping("/game/getUpdates")
	public void getGameUpdates(@Payload User user) {
		GameData data = gameService.getGameForUser(user.gameId, user.userId);
		messagingTemplate.convertAndSend("/game/updates/%s/%s".formatted(user.gameId, user.userId), data);
	}
	
	@MessageMapping("/game/updates")
	public void onGameUpdate(@Payload GameUpdate update) {
		boolean success = gameService.handleUpdate(update);
		//if move made update all
		if (success) {
			Game g = gameService.getGameById(update.user.gameId);
			if (g == null) return;
			List<String> playerIds = new ArrayList<>();
			g.getPlayers().forEach((player) -> playerIds.add(player.getUserId()));
			if (g.isWin()) {
				playerIds.forEach((userId) -> messagingTemplate.convertAndSend(
						"/game/updates/%s/%s".formatted(update.user.gameId, userId), "gameEnd"));
			} else {
				playerIds.forEach((userId) -> {
					if (!userId.equals(update.user.userId)) {
						messagingTemplate.convertAndSend("/game/updates/%s/%s".formatted(update.user.gameId, userId),
						                                 "gameUpdated");
					} else {
						GameData data = gameService.getGameForUser(update.user.gameId, userId);
						messagingTemplate.convertAndSend("/game/updates/%s/%s".formatted(update.user.gameId, userId),
						                                 data);
					}
				});
			}
		} else { // update only current user
			GameData data = gameService.getGameForUser(update.user.gameId, update.user.userId);
			messagingTemplate.convertAndSend("/game/updates/%s/%s".formatted(update.user.gameId, update.user.userId),
			                                 data);
		}
	}
	
	@MessageMapping("/game/saveBoard")
	public void onSaveBoard(@Payload GameUpdate update) {
		gameService.saveBoard(update);
		GameData data = gameService.getGameForUser(update.user.gameId, update.user.userId);
		messagingTemplate.convertAndSend("/game/updates/%s/%s".formatted(update.user.gameId, update.user.userId), data);
	}
	
	@GetMapping("/win")
	public String onWin(@RequestParam("gameId") String gameId, Model model) {
		List<ScoreUser> users = gameService.getUserScoresByGameId(gameId);
		model.addAttribute("users", users);
		return "win";
	}
	
	@PostMapping(value = "/game/leave", consumes = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<Void> gameLeave(@RequestBody String userText) {
		ObjectMapper mapper = new ObjectMapper();
		User user = null;
		try {
			user = mapper.readValue(userText, User.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		gameService.userLeave(user);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	public record User(String userId, String gameId) {}
	
	public record PlayerData(String id, String username, String image) {}
	
	public record GameData(List<PlayerData> players, String currentPlayer, Table table, List<List<Tile>> tiles) {}
	
	public record GameUpdate(User user, Table table, List<List<Tile>> tiles) {}
	
	public record ScoreUser(PlayerData playerData, int score) {}
}
