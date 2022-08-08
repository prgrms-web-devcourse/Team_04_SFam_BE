package com.kdt.team04.domain.auth.dto;

public record JwtToken(String header, String token, int expirySeconds) {
}
