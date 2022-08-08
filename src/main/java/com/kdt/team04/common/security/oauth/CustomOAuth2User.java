package com.kdt.team04.common.security.oauth;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.kdt.team04.domain.user.Role;

public class CustomOAuth2User implements OAuth2User, Serializable {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private final Set<GrantedAuthority> authorities;

	private final Map<String, Object> attributes;

	private final String nameAttributeKey;

	private final Long userId;
	private final String username;
	private final String email;
	private final Role role;

	public CustomOAuth2User(Set<GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey,
		Long userId, String username, String email, Role role) {
		this.authorities = authorities;
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.role = role;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getName() {
		return this.getAttribute(this.nameAttributeKey).toString();
	}

	public Long userId() {
		return userId;
	}

	public String username() {
		return username;
	}

	public String email() {
		return email;
	}

	public Role role() {
		return role;
	}
}
