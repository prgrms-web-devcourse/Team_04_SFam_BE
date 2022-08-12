package com.kdt.team04.domain.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.kdt.team04.common.config.QueryDslConfig;
import com.kdt.team04.configure.JpaAuditConfig;
import com.kdt.team04.domain.user.entity.User;

@Import({QueryDslConfig.class, JpaAuditConfig.class})
@DataJpaTest
class UserRepositoryTest {
	@Autowired
	TestEntityManager entityManager;

	@Autowired
	UserRepository userRepository;

	@Test
	@DisplayName("닉네임 수정 성공")
	void updateNickname() {
		//given
		String nickname = "modifiedNick";
		User user = getDummyUser(nickname);

		//when
		userRepository.save(user);

		//then
		assertThat(user.getNickname()).isEqualTo(nickname);
	}

	@Test
	@DisplayName("닉네임 _는 허용된다.")
	void updateNicknameSuccessUnderScore() {
		//given
		String nickname = "nickname_";
		User user = getDummyUser(nickname);

		//when
		userRepository.save(user);

		//then
		assertThat(user.getNickname()).isEqualTo(nickname);
	}

	@Test
	@DisplayName("닉네임 변경 시 .은 허용된다.")
	void updateNicknameSuccessDot() {
		//given
		String nickname = "nickname.";
		User user = getDummyUser(nickname);

		//when
		userRepository.save(user);

		//then
		assertThat(user.getNickname()).isEqualTo(nickname);
	}

	@Test
	@DisplayName("닉네임 변경 시 #은 허용된다.")
	void updateNicknameSuccessSharp() {
		//given
		User user = getDummyUser("nickname#");

		//when
		userRepository.save(user);

		//then
		assertThat(user.getNickname()).isEqualTo(user.getNickname());
	}

	@Test
	@DisplayName("닉네임 변경 시 한글 자음만 있다면 실패한다.")
	void updateNicknameFail2() {
		//given
		User user = getDummyUser("ㅁㅁㅁㅁ");

		//when, then
		assertThatThrownBy(() -> userRepository.save(user)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("닉네임에 '-' 가 포함되면 실패한다.")
	void updateNicknameFail3() {
		//given
		User user = getDummyUser("hello-");

		//when, then
		assertThatThrownBy(() -> userRepository.save(user)).isInstanceOf(ConstraintViolationException.class);
	}

	private User getDummyUser(String nickName) {
		return User.builder()
			.username("test1234")
			.password("$2a$12$VBMdI3AHeZK.1iPAK97kaO1K/YPNjoTjBjEfolydYMXpFHpr1ZljS")
			.nickname(nickName)
			.build();
	}
}
