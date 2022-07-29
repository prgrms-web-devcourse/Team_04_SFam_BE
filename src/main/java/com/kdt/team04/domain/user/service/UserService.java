package com.kdt.team04.domain.user.service;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.domain.match.review.dto.MatchReviewResponse;
import com.kdt.team04.domain.match.review.service.MatchReviewGiverService;
import com.kdt.team04.domain.team.dto.TeamResponse;
import com.kdt.team04.domain.team.service.TeamGiverService;
import com.kdt.team04.domain.user.dto.UserRequest;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.entity.Location;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final MatchReviewGiverService matchReviewGiver;
	private final TeamGiverService teamGiver;

	public UserService(UserRepository userRepository, MatchReviewGiverService matchReviewGiver, TeamGiverService teamGiver) {
		this.userRepository = userRepository;
		this.matchReviewGiver = matchReviewGiver;
		this.teamGiver = teamGiver;
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

		MatchReviewResponse.TotalCount review = matchReviewGiver.findTotalReviewByUserId(id);
		List<TeamResponse.SimpleResponse> teams = teamGiver.findAllByTeamMemberUserId(id);

		return new UserResponse.FindProfile(
			foundUser.getNickname(),
			review,
			teams
		);
	}

	public List<UserResponse.UserFindResponse> findAllByNickname(String nickname) {
		return userRepository.findByNicknameContaining(nickname).stream()
			.map(
				user -> new UserResponse.UserFindResponse(
					user.getId(),
					user.getUsername(),
					user.getNickname()
				)
			)
			.toList();
	}

	public UserResponse findById(Long id) {
		User foundUser = this.userRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND,
				MessageFormat.format("UserId = {0}", id)));

		return new UserResponse(
			foundUser.getId(),
			foundUser.getUsername(),
			foundUser.getPassword(),
			foundUser.getNickname()
		);
	}

	@Transactional
	public UserResponse.UpdateLocationResponse updateLocation(Long targetId,
		UserRequest.UpdateLocationRequest request) {
		User foundUser = userRepository.findById(targetId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
		foundUser.updateLocation(new Location(request.latitude(), request.longitude()));

		return new UserResponse.UpdateLocationResponse(request.latitude(), request.longitude());
	}
}