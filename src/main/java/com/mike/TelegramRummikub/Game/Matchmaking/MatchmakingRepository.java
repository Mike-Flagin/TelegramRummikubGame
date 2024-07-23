package com.mike.TelegramRummikub.Game.Matchmaking;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MatchmakingRepository extends MongoRepository<MatchmakingUser, String> {
	MatchmakingUser findFirstMatchmakingUserByUserId(String userId);
	
	@Query("{ 'gameId' : { $not: { $regex: ?0 } } }")
	List<String> findGameIdNotLikeOrderByGameId(String gameId);
	
	List<MatchmakingUser> findMatchmakingUserByGameId(String gameId);
	
	int countByGameId(String s);
}
