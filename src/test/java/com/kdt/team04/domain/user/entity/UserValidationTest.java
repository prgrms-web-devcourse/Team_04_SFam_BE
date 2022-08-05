package com.kdt.team04.domain.user.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kdt.team04.common.security.PasswordEncoderConfig;
import com.kdt.team04.common.security.jwt.Jwt;
import com.kdt.team04.configure.TestQueryDslConfig;
import com.kdt.team04.configure.WebSecurityTestConfigure;
import com.kdt.team04.domain.user.repository.UserRepository;

@DataJpaTest
@Import({WebSecurityTestConfigure.class, TestQueryDslConfig.class, PasswordEncoderConfig.class})
class UserValidationTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@MockBean
	Jwt jwt;

	@Test
	@DisplayName("User 추가 성공 테스트")
	void testSaveSuccess() {
		//given
		String encodedPassword = passwordEncoder.encode("@Password12");
		User user = new User(
			"test1234",
			"nickname",
			encodedPassword
		);

		//when
		User savedMember = userRepository.save(user);

		//then
		assertThat(savedMember.getId()).isNotNull();
		assertThat(savedMember.getUsername()).isEqualTo(user.getUsername());
		assertThat(savedMember.getPassword()).isEqualTo(user.getPassword());
		assertThat(savedMember.getNickname()).isEqualTo(user.getNickname());

	}

	@Test
	@DisplayName("Username blank일 때 유저 생성 실패")
	void testSaveWithBlankUsername() {
		//given
		String encodedPassword = passwordEncoder.encode("@Password12");
		User user = new User(
			"",
			"nickname",
			encodedPassword
		);

		//when, then
		assertThatThrownBy(() -> userRepository.save(user)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("Username 정규식 위반일 때 유저 생성 실패")
	void testSaveWithViolateRegex() {
		//given
		String encodedPassword = passwordEncoder.encode("@Password12");
		User user = new User(
			"VIOLATE",
			"nickname",
			encodedPassword
		);

		//when, then
		assertThatThrownBy(() -> userRepository.save(user)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("Password blank일 때 유저 생성 실패")
	void testSaveWithBlankPassword() {
		//given
		User user = new User(
			"test12345",
			"nickname",
			""
		);

		//when, then
		assertThatThrownBy(() -> userRepository.save(user)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("nickname blank일 때 유저 생성 실패")
	void testSaveWithBlankNickname() {
		//given
		String encodedPassword = passwordEncoder.encode("@Password12");
		User user = new User(
			"test12345",
			"",
			encodedPassword
		);

		//when, then
		assertThatThrownBy(() -> userRepository.save(user)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("nickname 너무 짧을 때 유저 생성 실패")
	void testSaveWithShortNickname() {
		//given
		String encodedPassword = passwordEncoder.encode("@Password12");
		User user = new User(
			"test12345",
			"1",
			encodedPassword
		);

		//when, then
		assertThatThrownBy(() -> userRepository.save(user)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("Username 너무 짧을 때 유저 생성 실패")
	void testSaveWithShortUsername() {
		//given
		String encodedPassword = passwordEncoder.encode("@Password12");
		User user = new User(
			"short",
			"nickname",
			encodedPassword
		);

		//when, then
		assertThatThrownBy(() -> userRepository.save(user)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("Username 너무 길 때 유저 생성 실패")
	void testSaveWithLongUsername() {
		//given
		String encodedPassword = passwordEncoder.encode("@Password12");
		User user = new User(
			"toolongtoolongtoolongtoolongtoolongtoolongtoolongtoolongtoolongtoolongtoolongtoolongtoolongtoolongtoolongtoolongtoolongtoolong",
			"nickname",
			encodedPassword
		);

		//when, then
		assertThatThrownBy(() -> userRepository.save(user)).isInstanceOf(ConstraintViolationException.class);
	}
}
