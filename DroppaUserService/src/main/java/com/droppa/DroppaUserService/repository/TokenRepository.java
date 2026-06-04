package com.droppa.DroppaUserService.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.droppa.DroppaUserService.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Integer> {

  List<Token> findAllByUserIdAndExpiredFalseAndRevokedFalse(Integer id);

  Optional<Token> findByToken(String token);
  
  Optional<Token> findByUserId(int userId);
}
