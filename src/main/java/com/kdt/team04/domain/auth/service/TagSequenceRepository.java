package com.kdt.team04.domain.auth.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.auth.entity.TagSequence;

public interface TagSequenceRepository extends JpaRepository<TagSequence, String> {
}
