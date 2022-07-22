package com.kdt.team04.domain.auth.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.kdt.team04.domain.BaseEntity;

@Entity
public class Token extends BaseEntity {
	@Id
	private String token;

	private Long userId;

	protected Token() {
	}

	public Token(String token, Long userId) {
		this.token = token;
		this.userId = userId;
	}

	public String token() {
		return token;
	}

	public Long getUserId() {
		return userId;
	}
}
