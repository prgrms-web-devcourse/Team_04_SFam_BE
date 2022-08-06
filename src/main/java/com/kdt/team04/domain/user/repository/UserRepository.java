package com.kdt.team04.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.team04.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	List<User> findByNicknameContaining(String nickname);

	Boolean existsByNickname(String nickname);

	Boolean existsByUsername(String username);
}
