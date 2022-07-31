package com.kdt.team04.domain.matches.proposal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.matches.proposal.entity.MatchChat;

public interface MatchChatRepository extends JpaRepository<MatchChat, Long>, MatchChatRepositoryCustom {
}