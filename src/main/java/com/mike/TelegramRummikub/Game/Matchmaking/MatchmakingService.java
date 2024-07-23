package com.mike.TelegramRummikub.Game.Matchmaking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MatchmakingService {
	//TODO: global matchmaking
	private static final int MAX_USERS = 4;
	@Autowired
	private MatchmakingRepository repository;
	
	/**
	 * @param chatId   chatId from Telegram
	 * @param userId   userId from Telegram
	 * @param username username of user from Telegram
	 * @return User added or null if user already exists
	 */
	public MatchmakingUser addUser(String chatId, String userId, String username) {
		String gameId = null;
		MatchmakingUser user = repository.findMatchmakingUserByUserId(userId);
		if (user != null) return null;
		boolean isGlobal = !chatId.startsWith("-");
		if (isGlobal) {
			List<String> games = repository.findGameIdNotLikeOrderByGameId("-%");
			if (games.size() == 0) gameId = "1";
			else {
				for (String game : games) {
					if (repository.getGameUsersCountByGameId(game) < MAX_USERS) {
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
			if (repository.getGameUsersCountByGameId(chatId) < MAX_USERS) {
				gameId = chatId;
			} else {
				int i = 1;
				while (repository.getGameUsersCountByGameId(chatId + i) == MAX_USERS) i++;
				gameId = chatId + i;
			}
		}
		MatchmakingUser newUser = new MatchmakingUser(gameId, userId, username);
		repository.save(newUser);
		return newUser;
	}
	
	public MatchmakingUser getUserById(String userId) {
		return repository.findMatchmakingUserByUserId(userId);
	}
	
	public List<MatchmakingUser> getUsersByGameId(String gameId) {
		return repository.findMatchmakingUserByGameId(gameId);
	}
	
	public void removeUser(MatchmakingUser matchmakingUser) {
		matchmakingUser.deleteImg();
		repository.delete(matchmakingUser);
	}
}
