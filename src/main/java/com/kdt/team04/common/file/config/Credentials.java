package com.kdt.team04.common.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cloud.aws.credentials")
public class Credentials {
	private String accessKey;
	private String secretKey;

	public String getAccessKey() {
		return accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
}
