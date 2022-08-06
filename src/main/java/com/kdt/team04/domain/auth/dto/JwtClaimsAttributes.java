package com.kdt.team04.domain.auth.dto;

import com.kdt.team04.domain.user.Role;

public record JwtClaimsAttributes(
	Long id,
	String username,
	String email,
	Role role
) {
}