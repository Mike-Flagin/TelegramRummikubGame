package com.mike.TelegramRummikub.Game;

import com.mike.TelegramRummikub.Game.Matchmaking.MatchmakingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GameController {
	@Autowired
	private MatchmakingService matchmakingService;
	private final SimpMessagingTemplate messagingTemplate;
	@Autowired
	public GameController(SimpMessagingTemplate messagingTemplate) {this.messagingTemplate = messagingTemplate;}
	
	@GetMapping("/game/{gameId}")
	public void startGame(@PathVariable("gameId") String gameId, @RequestParam("userId") String userId) {
	
	}
}
