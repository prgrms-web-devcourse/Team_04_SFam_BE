package com.kdt.team04.domain.user.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.team04.common.exception.EntityNotFoundException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.common.file.ImagePath;
import com.kdt.team04.common.aws.s3.S3Uploader;
import com.kdt.team04.domain.matches.review.dto.response.MatchReviewTotalResponse;
import com.kdt.team04.domain.matches.review.service.MatchReviewGiverService;
import com.kdt.team04.domain.teams.team.dto.response.TeamSimpleResponse;
import com.kdt.team04.domain.teams.team.service.TeamGiverService;
import com.kdt.team04.domain.user.UserConverter;
import com.kdt.team04.domain.user.dto.request.CreateUserRequest;
import com.kdt.team04.domain.user.dto.request.UpdateUserLocationRequest;
import com.kdt.team04.domain.user.dto.request.UpdateUserRequest;
import com.kdt.team04.domain.user.dto.response.FindProfileResponse;
import com.kdt.team04.domain.user.dto.response.UpdateLocationResponse;
import com.kdt.team04.domain.user.dto.response.UserFindResponse;
import com.kdt.team04.domain.user.dto.response.UserResponse;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.entity.UserSettings;
import com.kdt.team04.domain.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final MatchReviewGiverService matchReviewGiver;
	private final TeamGiverService teamGiver;
	private final UserConverter userConverter;
	private final S3Uploader s3Uploader;

	public UserService(UserRepository userRepository, MatchReviewGiverService matchReviewGiver,
		TeamGiverService teamGiver, S3Uploader s3Uploader, UserConverter userConverter) {
		this.userRepository = userRepository;
		this.matchReviewGiver = matchReviewGiver;
		this.teamGiver = teamGiver;
		this.userConverter = userConverter;
		this.s3Uploader = s3Uploader;
	}

	public UserResponse findByUsername(String username) {
		User foundUser = this.userRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND,
				MessageFormat.format("Username = {0}", username)));

		return userConverter.toUserResponse(foundUser);
	}

	public UserResponse findByEmail(String email) {
		User foundUser = this.userRepository.findByEmail(email)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND,
				MessageFormat.format("email = {0}", email)));

		return userConverter.toUserResponse(foundUser);
	}

	public UserResponse findById(Long id) {
		User foundUser = this.userRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND,
				MessageFormat.format("UserId = {0}", id)));

		return userConverter.toUserResponse(foundUser);
	}

	public FindProfileResponse findProfileById(Long id) {
		User foundUser = userRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND,
				MessageFormat.format("UserId = {0}", id)));

		MatchReviewTotalResponse review = matchReviewGiver.findTotalReviewByUserId(id);
		List<TeamSimpleResponse> teams = teamGiver.findAllByTeamMemberUserId(id);

		return new FindProfileResponse(
			foundUser.getNickname(),
			foundUser.getProfileImageUrl(),
			review,
			teams
		);
	}

	@Transactional
	public Long create(CreateUserRequest request) {
		User user = userConverter.toUser(request);

		return userRepository.save(user).getId();
	}

	public List<UserFindResponse> findAllByNickname(String nickname) {
		return userRepository.findByNicknameContaining(nickname).stream()
			.map(
				user -> new UserFindResponse(
					user.getId(),
					user.getUsername(),
					user.getNickname(),
					user.getProfileImageUrl()
				)
			)
			.toList();
	}

	@Transactional
	public UpdateLocationResponse updateLocation(Long targetId,
		UpdateUserLocationRequest request) {
		User foundUser = this.userRepository.findById(targetId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND,
				MessageFormat.format("UserId = {0}", targetId)));
		foundUser.updateSettings(new UserSettings(request.latitude(), request.longitude(), request.searchDistance()));

		return new UpdateLocationResponse(request.latitude(), request.longitude(), request.searchDistance());
	}

	public Boolean usernameDuplicationCheck(String username) {
		return userRepository.existsByUsername(username);
	}

	public Boolean nicknameDuplicationCheck(String nickname) {
		return userRepository.existsByNickname(nickname);
	}

	public void update(Long targetId, UpdateUserRequest request) {
		User foundUser = this.userRepository.findById(targetId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND,
				MessageFormat.format("UserId = {0}", targetId)));
		foundUser.update(request.nickname(), request.email(), request.profileImageUrl());
	}

	@Transactional
	public void uploadProfile(Long id, MultipartFile file) {
		User foundUser = this.userRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND,
				MessageFormat.format("UserId = {0}", id)));

		Optional.ofNullable(foundUser.getProfileImageUrl())
			.ifPresentOrElse(
				key -> s3Uploader.uploadByKey(file.getResource(), key),
				() -> {
					String key = s3Uploader.uploadByPath(file.getResource(), ImagePath.USERS_PROFILES);
					foundUser.updateImageUrl(key);
				}
			);
	}

}
