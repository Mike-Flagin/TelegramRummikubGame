package com.mike.TelegramRummikub.Game.Matchmaking;

import com.mike.TelegramRummikub.Game.Game;
import com.mike.TelegramRummikub.Game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MatchmakingService {
	//TODO: global matchmaking
	private static final int MAX_USERS = 4;
	@Autowired
	private MatchmakingRepository repository;
	@Autowired
	private GameService gameService;
	
	/**
	 * @param chatId   chatId from Telegram
	 * @param userId   userId from Telegram
	 * @param username username of user from Telegram
	 * @return User added or null if user already exists
	 */
	public MatchmakingUser addUser(String chatId, String userId, String username) {
		String gameId = null;
		MatchmakingUser user = repository.findFirstMatchmakingUserByUserId(userId);
		if (user != null) return null;
		boolean isGlobal = !chatId.startsWith("-");
		if (isGlobal) {
			List<String> games = repository.findGameIdNotLikeOrderByGameId("-%");
			if (games.isEmpty()) {
				gameId = "1";
			} else {
				for (String game : games) {
					if (repository.countByGameId(game) < MAX_USERS) {
						gameId = game;
						break;
					}
				}
				if (gameId == null) {
					for (int i = 1; i <= games.size() + 1; i++) {
						String temp = String.valueOf(i);
						if (!games.contains(temp)) {
							gameId = temp;
							break;
						}
					}
				}
			}
		} else {
			if (repository.countByGameId(chatId) < MAX_USERS) {
				gameId = chatId;
			} else {
				int i = 1;
				while (repository.countByGameId(chatId + i) == MAX_USERS) i++;
				gameId = chatId + i;
			}
		}
		MatchmakingUser newUser = new MatchmakingUser(gameId, userId, username);
		repository.save(newUser);
		return newUser;
	}
	
	public MatchmakingUser getUserById(String userId) {
		return repository.findFirstMatchmakingUserByUserId(userId);
	}
	
	public List<MatchmakingUser> getUsersByGameId(String gameId) {
		return repository.findMatchmakingUserByGameId(gameId);
	}
	
	public void removeUser(MatchmakingUser matchmakingUser) {
		matchmakingUser.deleteImage();
		repository.delete(matchmakingUser);
	}
	
	public void createGame(String gameId) {
		//create new game
		List<MatchmakingUser> users = repository.findMatchmakingUserByGameId(gameId);
		gameService.createGame(gameId, users);
		
		//delete from matchmaking
		repository.deleteAll(users);
	}
	
	//TODO: if matchmaking algorithm changes remake it
	public boolean isUserAlreadyInGame(String chatId, String userId) {
		Game g = gameService.getGameById(chatId);
		if (g == null) return false;
		return g.getPlayers().stream().anyMatch((user) -> user.getUserId().equals(userId));
	}
}
