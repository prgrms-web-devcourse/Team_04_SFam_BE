package com.kdt.team04.domain.auth.dto;

public record TokenDto(String header, String token, int expirySecond) {
}
