package com.mike.TelegramRummikub.Game.Matchmaking;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

@Repository
@Transactional
@EnableTransactionManagement
public interface MatchmakingRepository extends ListCrudRepository<MatchmakingUser, String> {
	MatchmakingUser findMatchmakingUserByUserId(String userId);
	@Query("select u.gameId from MatchmakingUser u where u.gameId not like ?1")
	List<String> findGameIdNotLikeOrderByGameId(String gameId);
	List<MatchmakingUser> findMatchmakingUserByGameId(String gameId);
	@Query("select Count(u.userId) from MatchmakingUser u where u.gameId = ?1")
	int getGameUsersCountByGameId(String s);
}
