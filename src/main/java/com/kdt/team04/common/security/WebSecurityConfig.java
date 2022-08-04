package com.kdt.team04.common.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.kdt.team04.common.redis.RedisService;
import com.kdt.team04.common.security.jwt.Jwt;
import com.kdt.team04.common.security.jwt.JwtAuthenticationFilter;
import com.kdt.team04.common.security.jwt.JwtConfig;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties({JwtConfig.class, SecurityConfigProperties.class})
public class WebSecurityConfig {

	private final SecurityConfigProperties securityConfigProperties;

	public WebSecurityConfig(SecurityConfigProperties securityConfigProperties) {
		this.securityConfigProperties = securityConfigProperties;
	}

	@Bean
	public Jwt jwt(JwtConfig jwtConfig) {
		return new Jwt(jwtConfig);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public JwtAuthenticationFilter jwtAuthenticationFilter(Jwt jwt, RedisService redisService) {
		return new JwtAuthenticationFilter(jwt, redisService);
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.antMatchers(HttpMethod.GET, this.securityConfigProperties.patterns().ignoring().get("GET"))
			.antMatchers(HttpMethod.POST, this.securityConfigProperties.patterns().ignoring().get("POST"))
			.antMatchers(HttpMethod.PATCH, this.securityConfigProperties.patterns().ignoring().get("PATCH"))
			.antMatchers(HttpMethod.DELETE, this.securityConfigProperties.patterns().ignoring().get("DELETE"))
			.antMatchers(HttpMethod.PUT, this.securityConfigProperties.patterns().ignoring().get("PUT"))
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, Jwt jwt, RedisService redisService) throws Exception {
		http
			.authorizeRequests()
			.antMatchers(HttpMethod.GET, this.securityConfigProperties.patterns().permitAll().get("GET")).permitAll()
			.antMatchers(HttpMethod.POST, this.securityConfigProperties.patterns().permitAll().get("POST")).permitAll()
			.antMatchers(HttpMethod.PATCH, this.securityConfigProperties.patterns().permitAll().get("PATCH")).permitAll()
			.antMatchers(HttpMethod.DELETE, this.securityConfigProperties.patterns().permitAll().get("DELETE")).permitAll()
			.antMatchers(HttpMethod.PUT, this.securityConfigProperties.patterns().permitAll().get("PUT")).permitAll()
			.antMatchers(HttpMethod.OPTIONS, this.securityConfigProperties.patterns().permitAll().get("OPTIONS")).permitAll()
			.anyRequest().authenticated()
			.and()
			.formLogin()
				.disable()
			.csrf()
				.disable()
			.headers()
				.disable()
			.httpBasic()
				.disable()
			.rememberMe()
				.disable()
			.logout()
				.disable()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.addFilterBefore(jwtAuthenticationFilter(jwt, redisService), UsernamePasswordAuthenticationFilter.class)
		;

		return http.build();
	}

}
