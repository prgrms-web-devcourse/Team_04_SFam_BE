package com.kdt.team04.domain.user.service;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.domain.user.dto.UserRequest;
import com.kdt.team04.domain.user.dto.UserResponse;
import com.kdt.team04.domain.user.entity.Location;
import com.kdt.team04.domain.user.entity.User;
import com.kdt.team04.domain.user.repository.UserRepository;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

	@Autowired
	UserService userService;

	@Autowired
	EntityManager entityManager;

	@Autowired
	UserRepository userRepository;

	@Test
	@DisplayName("새로운 유저는 위치정보가 처음엔 null이다.")
	void newbieLocationIsNull() {
		//given
		UserRequest.CreateRequest request = new UserRequest.CreateRequest("test1234", "@Test1234", "nickname");

		//when
		Long newUserId = userService.create(request);
		User foundUser = entityManager.find(User.class, newUserId);

		//then
		Assertions.assertThat(foundUser.getLocation()).isNull();
	}

	@Test
	@DisplayName("유저의 위도 경도를 각각 1.2, 2.2로 변경한다.")
	void updateLocation() {
		//given
		User user = new User("test1234", "nickname", "$2a$12$VBMdI3AHeZK.1iPAK97kaO1K/YPNjoTjBjEfolydYMXpFHpr1ZljS");
		entityManager.persist(user);
		Location location = new Location(1.2, 2.2);
		UserRequest.UpdateLocationRequest request = new UserRequest.UpdateLocationRequest(1.2, 2.2);
		//when
		UserResponse.UpdateLocationResponse response = userService.updateLocation(user.getId(), request);
		User foundUser = entityManager.find(User.class, user.getId());

		//then
		Assertions.assertThat(foundUser.getLocation()).isNotNull();
		Assertions.assertThat(foundUser.getLocation().getLongitude()).isEqualTo(location.getLongitude());
		Assertions.assertThat(foundUser.getLocation().getLatitude()).isEqualTo(location.getLatitude());
		Assertions.assertThat(response.latitude()).isEqualTo(location.getLatitude());
		Assertions.assertThat(response.longitude()).isEqualTo(location.getLongitude());
	}
}
