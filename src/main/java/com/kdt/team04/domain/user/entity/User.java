package com.kdt.team04.domain.user.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.kdt.team04.domain.BaseEntity;

import lombok.Builder;

@Table(name = "users")
@Entity

public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String password;

	@NotBlank
	@Column(unique = true)
	@Pattern(regexp = "^[a-z0-9_]*$")
	@Size(min = 6, max = 24)
	private String username;

	@NotBlank
	@Size(min = 2, max = 16)
	@Column(unique = true)
	private String nickname;

	@Embedded
	private Location location;

	private String profileImageUrl;

	protected User() {
	}

	public User(String username, String nickname, String password) {
		this(null, password, username, nickname, null, null);
	}

	@Builder
	public User(Long id, String password, String username, String nickname,
		Location location, String profileImageUrl) {
		this.id = id;
		this.password = password;
		this.username = username;
		this.nickname = nickname;
		this.location = location;
		this.profileImageUrl = profileImageUrl;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getNickname() {
		return nickname;
	}

	public Location getLocation() {
		return location;
	}

	public void updateLocation(Location location) {
		this.location = location;
	}

	public void updateImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

}
