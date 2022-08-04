package com.kdt.team04.common.security.jwt;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.Builder;

public class Jwt {
	private final JwtConfig jwtConfigure;
	private final Algorithm algorithm;
	private final JWTVerifier jwtVerifier;

	public Jwt(JwtConfig jwtConfigure) {
		this.jwtConfigure = jwtConfigure;
		this.algorithm = Algorithm.HMAC512(jwtConfigure.clientSecret());
		this.jwtVerifier = JWT.require(algorithm)
			.withIssuer(this.jwtConfigure.issuer())
			.build();
	}

	public String generateAccessToken(Claims claims) {
		Date now = new Date();
		JWTCreator.Builder builder = JWT.create();

		builder.withSubject(claims.userId.toString());
		builder.withIssuer(jwtConfigure.issuer());
		builder.withIssuedAt(now);

		if (jwtConfigure.accessToken().expirySeconds() > 0) {
			builder.withExpiresAt(new Date(now.getTime() + jwtConfigure.accessToken().expirySeconds() * 1000L));
		}
		builder.withClaim("userId", claims.userId);
		builder.withClaim("username", claims.username);
		builder.withArrayClaim("roles", claims.roles);

		return builder.sign(this.algorithm);
	}

	public String generateRefreshToken() {
		Date now = new Date();
		JWTCreator.Builder builder = JWT.create();
		builder.withIssuer(this.jwtConfigure.issuer());
		builder.withIssuedAt(now);
		if (this.jwtConfigure.refreshToken().expirySeconds() > 0) {
			builder.withExpiresAt(new Date(now.getTime() + jwtConfigure.refreshToken().expirySeconds() * 1000L));
		}

		return builder.sign(this.algorithm);
	}

	public int getExpirySeconds() {
		return jwtConfigure.refreshToken().expirySeconds();
	}

	public Claims decode(String token) {
		return new Claims(JWT.decode(token));

	}

	public Claims verify(String token) {
		return new Claims(this.jwtVerifier.verify(token));

	}

	public List<GrantedAuthority> getAuthorities(Claims claims) {
		String[] roles = claims.roles;

		return roles == null || roles.length == 0
			? Collections.emptyList()
			: Arrays.stream(roles)
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());
	}

	public JwtConfig.TokenProperties accessTokenProperties() {
		return this.jwtConfigure.accessToken();
	}

	public JwtConfig.TokenProperties refreshTokenProperties() {
		return this.jwtConfigure.refreshToken();
	}

	public static class Claims {
		Long userId;
		String username;
		String[] roles;
		Date iat;
		Date exp;

		private Claims() {
		}

		Claims(DecodedJWT decodedJWT) {
			Claim userId = decodedJWT.getClaim("userId");
			if (!userId.isNull()) {
				this.userId = userId.asLong();
			}
			Claim username = decodedJWT.getClaim("username");
			if (!username.isNull()) {
				this.username = username.asString();
			}
			Claim roles = decodedJWT.getClaim("roles");
			if (!roles.isNull()) {
				this.roles = roles.asArray(String.class);
			}
			this.iat = decodedJWT.getIssuedAt();
			this.exp = decodedJWT.getExpiresAt();
		}

		@Builder
		Claims(Long userId, String username, String[] roles, Date iat, Date exp) {
			this.userId = userId;
			this.username = username;
			this.roles = roles;
			this.iat = iat;
			this.exp = exp;
		}
	}

}
