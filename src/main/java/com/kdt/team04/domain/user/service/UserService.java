package com.kdt.team04.domain.user.service;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.user.dto.UserRequest;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public UserResponse findByUsername(String username) {
		User foundUser = this.userRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND,
				MessageFormat.format("Username = {0}", username)));

		return new UserResponse(
			foundUser.getId(),
			foundUser.getUsername(),
			foundUser.getPassword(),
			foundUser.getNickname()
		);
	}

	@Transactional
	public Long create(UserRequest.CreateRequest request) {
		return userRepository.save(new User(request.username(), request.nickname(), request.password())).getId();
	}

	public UserResponse.FindProfile findProfileById(Long id) {
		User foundUser = userRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND,
				MessageFormat.format("UserId = {0}", id)));

		return new UserResponse.FindProfile(
			foundUser.getId(),
			foundUser.getUsername(),
			foundUser.getNickname()
		);
	}
}
