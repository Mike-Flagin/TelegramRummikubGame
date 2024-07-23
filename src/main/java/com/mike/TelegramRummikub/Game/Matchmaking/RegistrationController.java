package com.mike.TelegramRummikub.Game.Matchmaking;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

@Controller
public class RegistrationController {
	@Autowired
	private MatchmakingService matchmakingService;
	private final SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	public RegistrationController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}
	
	@Autowired
	private LocaleResolver localeResolver;
	
	@GetMapping("/register")
	public String registerUser(HttpServletRequest request, HttpServletResponse response,
	                           @RequestParam("chat") String chat, @RequestParam("user") String user,
	                           @RequestParam("username") String username, @RequestParam("language") String language) {
		MatchmakingUser newUser = matchmakingService.addUser(chat, user, username);
		if (newUser != null) {
			messagingTemplate.convertAndSend("/registration/updates/%s".formatted(newUser.getGameId()),
			                                 new User(true, newUser));
		} else {
			newUser = matchmakingService.getUserById(user);
		}
		Locale locale = new Locale(language);
		localeResolver.setLocale(request, response, locale);
		return "redirect:/registration?gameId=%s&userId=%s".formatted(newUser.getGameId(), newUser.getUserId());
	}
	
	@GetMapping("/registration")
	public String registration(@RequestParam("gameId") String gameId, @RequestParam("userId") String userId,
	                           Model model) {
		List<MatchmakingUser> registeredUsers = matchmakingService.getUsersByGameId(gameId);
		model.addAttribute("users", registeredUsers);
		if (userId.equals(registeredUsers.get(0).getUserId())) model.addAttribute("mainUser", true);
		return "registration";
	}
	
	@MessageMapping("/registration/leave")
	public void registrationUpdate(@Payload User user) {
		MatchmakingUser matchmakingUser = matchmakingService.getUserById(user.user.getUserId());
		messagingTemplate.convertAndSend("/registration/updates/%s".formatted(user.user.getGameId()),
		                                 new User(false, matchmakingUser));
		matchmakingService.removeUser(matchmakingUser);
	}
	
	@MessageMapping("/registration/start")
	public void gameStart(@Payload String gameId) {
		messagingTemplate.convertAndSend("/registration/start/%s".formatted(gameId), "");
	}
	
	@GetMapping(value = "/images/{*imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getImage(@PathVariable String imageId) {
		try {
			byte[] image = Files.readAllBytes(Path.of(imageId));
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			return new ResponseEntity<>(image, headers, HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * @param add  if true - user added, else - removed
	 * @param user
	 */
	private record User(boolean add, MatchmakingUser user) {}
}
//TODO: image for users without image, fix when mainUser leaves button must be added to another user,
// leave when page is closed