package com.kdt.team04.domain.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.auth.dto.model.entity.Token;

public interface TokenRepository extends JpaRepository<Token, String> {
	Optional<Token> findByUserId(Long userId);
}
