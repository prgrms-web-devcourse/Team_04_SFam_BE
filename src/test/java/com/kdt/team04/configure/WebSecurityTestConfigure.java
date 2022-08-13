
package com.kdt.team04.configure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import com.kdt.team04.common.config.CorsConfigProperties;
import com.kdt.team04.common.security.SecurityConfigProperties;
import com.kdt.team04.common.security.WebSecurityConfig;
import com.kdt.team04.domain.auth.service.JpaTokenService;

@Import({WebSecurityConfig.class, JpaTokenService.class})
@EnableConfigurationProperties({SecurityConfigProperties.class, CorsConfigProperties.class})
public class WebSecurityTestConfigure {
}

