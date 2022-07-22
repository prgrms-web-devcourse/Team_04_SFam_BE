package com.kdt.team04.domain.auth.service;

import com.kdt.team04.domain.auth.dto.AuthRequest;
import com.kdt.team04.domain.auth.dto.AuthResponse;

public interface AuthService {
	AuthResponse.SignInResponse signIn(String username, String password);

	AuthResponse.SignUpResponse signUp(AuthRequest.SignUpRequest request);

}
