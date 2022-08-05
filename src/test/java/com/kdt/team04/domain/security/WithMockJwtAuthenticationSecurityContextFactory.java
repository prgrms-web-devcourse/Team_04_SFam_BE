package com.kdt.team04.domain.security;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.kdt.team04.common.security.jwt.JwtAuthentication;
import com.kdt.team04.common.security.jwt.JwtAuthenticationToken;

public class WithMockJwtAuthenticationSecurityContextFactory implements WithSecurityContextFactory<WithMockJwtAuthentication> {

	@Override
	public SecurityContext createSecurityContext(WithMockJwtAuthentication annotation) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		JwtAuthenticationToken authentication = new JwtAuthenticationToken(
			new JwtAuthentication(annotation.token(), annotation.id(), annotation.username(), annotation.email()),
			null,
			createAuthorityList(annotation.role().getKey())
		);
		context.setAuthentication(authentication);
		return context;
	}
}
