package com.kdt.team04.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.web.server.Cookie;

@ConstructorBinding
@ConfigurationProperties(prefix = "cookie")
public record CookieConfigProperties(Boolean secure, Cookie.SameSite sameSite, String domain) {
}
