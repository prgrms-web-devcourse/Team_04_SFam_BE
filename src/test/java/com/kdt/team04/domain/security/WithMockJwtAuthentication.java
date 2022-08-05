package com.kdt.team04.domain.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

import com.kdt.team04.domain.user.Role;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockJwtAuthenticationSecurityContextFactory.class)
public @interface WithMockJwtAuthentication {

	String token() default "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwicm9sZXMiOlsiVVNFUiJdLCJpc3MiOiJzZmFtIiwiZXhwIjoxNjU4NTkyNTYyLCJpYXQiOjE2NTg1OTI1MzIsInVzZXJJZCI6MSwidXNlcm5hbWUiOiJ0ZXN0MDAifQ.FDkOUzhLvKOYFmjOxRtF-dRDSO2BkoplJTMIyhp0c0ajxOLeZbKuekSyySnCnjVvv_f0Qx8T7a3ZS2OlaSGiDQ";
	long id() default 1L;
	String username() default "test00";
	Role role() default Role.USER;
	String email() default "test00@google.com";
}
