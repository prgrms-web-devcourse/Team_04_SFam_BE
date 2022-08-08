package com.kdt.team04.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;
import com.kdt.team04.common.file.service.S3Uploader;
import com.kdt.team04.domain.user.Role;
import com.kdt.team04.domain.user.dto.request.UserCreateRequest;
import com.kdt.team04.domain.user.dto.request.UserUpdateLocationRequest;
import com.kdt.team04.domain.user.dto.response.UpdateLocationResponse;
import com.kdt.team04.domain.user.entity.Location;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.repository.UserRepository;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

	@Autowired
	UserService userService;

	@Autowired
	EntityManager entityManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@MockBean
	S3Uploader s3Uploader;

	@MockBean
	AmazonS3 amazonS3;

	@Test
	@DisplayName("새로운 유저는 위치정보가 처음엔 null이다.")
	void newbieLocationIsNull() {
		//given
		UserCreateRequest request = new UserCreateRequest("test1234", "@Test1234", "nickname",
			"test1234@gmail.com", null, Role.USER);

		//when
		Long newUserId = userService.create(request);
		User foundUser = entityManager.find(User.class, newUserId);

		//then
		Assertions.assertThat(foundUser.getUserSettings()).isNull();
	}

	@Test
	@DisplayName("유저의 위도 경도를 각각 1.2, 2.2로 변경한다.")
	void updateLocation() {
		//given
		User user = new User("test1234", "nickname", "$2a$12$VBMdI3AHeZK.1iPAK97kaO1K/YPNjoTjBjEfolydYMXpFHpr1ZljS");
		entityManager.persist(user);
		Location location = new Location(1.2, 2.2);
		Integer searchDistance = 10;
		UserUpdateLocationRequest request = new UserUpdateLocationRequest(1.2, 2.2, 10);
		//when
		UpdateLocationResponse response = userService.updateLocation(user.getId(), request);
		User foundUser = entityManager.find(User.class, user.getId());

		//then
		Assertions.assertThat(foundUser.getUserSettings()).isNotNull();
		Assertions.assertThat(foundUser.getUserSettings().getLocation().getLongitude()).isEqualTo(location.getLongitude());
		Assertions.assertThat(foundUser.getUserSettings().getLocation().getLatitude()).isEqualTo(location.getLatitude());
		Assertions.assertThat(foundUser.getUserSettings().getSearchDistance()).isEqualTo(searchDistance);
		Assertions.assertThat(response.latitude()).isEqualTo(location.getLatitude());
		Assertions.assertThat(response.longitude()).isEqualTo(location.getLongitude());
	}

	@Test
	@DisplayName("nickname이 중복되면 true를 반환한다.")
	void testNicknameDuplicationCheckTrue() {
		//given
		String password = "@test1234!";
		String encodedPassword = passwordEncoder.encode(password);

		User userA = User.builder()
			.username("test1234")
			.nickname("nickname1234")
			.password(encodedPassword)
			.build();
		entityManager.persist(userA);

		//when
		Boolean isDuplicated = userService.nicknameDuplicationCheck(userA.getNickname());

		//then
		assertThat(isDuplicated).isTrue();
	}

	@Test
	@DisplayName("nickname이 중복되지 않으면 false를 반환한다.")
	void testNicknameDuplicationCheckFalse() {
		//given
		String password = "@test1234!";
		String encodedPassword = passwordEncoder.encode(password);

		User userA = User.builder()
			.username("test1234")
			.nickname("nickname1235")
			.password(encodedPassword)
			.build();
		entityManager.persist(userA);

		//when
		Boolean isDuplicated = userService.nicknameDuplicationCheck("nickname1234");

		//then
		assertThat(isDuplicated).isFalse();
	}

	@Test
	@DisplayName("username이 중복되면 true를 반환한다.")
	void testUsernameDuplicationCheckTrue() {
		//given
		String password = "@test1234!";
		String encodedPassword = passwordEncoder.encode(password);

		User userA = User.builder()
			.username("test1234")
			.nickname("nickname1234")
			.password(encodedPassword)
			.build();
		entityManager.persist(userA);

		//when
		Boolean isDuplicated = userService.usernameDuplicationCheck(userA.getUsername());

		//then
		assertThat(isDuplicated).isTrue();
	}

	@Test
	@DisplayName("username이 중복되지 않으면 false를 반환한다.")
	void testUsernameDuplicationCheckFalse() {
		//given
		String password = "@test1234!";
		String encodedPassword = passwordEncoder.encode(password);

		User userA = User.builder()
			.username("test1234")
			.nickname("nickname1235")
			.password(encodedPassword)
			.build();
		entityManager.persist(userA);

		//when
		Boolean isDuplicated = userService.usernameDuplicationCheck("test123456");

		//then
		assertThat(isDuplicated).isFalse();
	}
}
