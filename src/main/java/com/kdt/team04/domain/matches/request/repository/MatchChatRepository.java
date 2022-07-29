package com.kdt.team04.domain.matches.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.matches.request.entity.MatchChat;

public interface MatchChatRepository extends JpaRepository<MatchChat, Long> {
}
