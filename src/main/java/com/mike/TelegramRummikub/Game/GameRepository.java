package com.mike.TelegramRummikub.Game;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<Game, String> {}
