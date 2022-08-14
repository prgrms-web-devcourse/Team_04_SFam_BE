package com.kdt.team04.domain.auth.dto.model.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.kdt.team04.domain.BaseEntity;

@Entity
public class Token extends BaseEntity {

	@Id
	private Long userId;

	private String token;

	private LocalDateTime expiryDate;

	protected Token() {
	}

	public Token(Long userId, String token, LocalDateTime expiryDate) {
		this.userId = userId;
		this.token = token;
		this.expiryDate = expiryDate;
	}

	//== 비지니스 로직 ==//
	public void updateToken(String token, LocalDateTime expiryDate) {
		this.token = token;
		this.expiryDate = expiryDate;
	}

	public String getToken() {
		return token;
	}

	public Long getUserId() {
		return userId;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("userId", userId)
			.append("token", token)
			.append("expiryDate", expiryDate)
			.toString();
	}
}
