
package com.kdt.team04.configure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import com.kdt.team04.common.security.SecurityConfigProperties;
import com.kdt.team04.common.security.WebSecurityConfig;

@Import({WebSecurityConfig.class})
@EnableConfigurationProperties(SecurityConfigProperties.class)
public class WebSecurityTestConfigure {
}

