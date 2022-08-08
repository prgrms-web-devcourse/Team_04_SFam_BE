package com.kdt.team04.common.aws.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "cloud.aws")
public record AwsConfigProperties(
	Credentials credentials,
	String region
) {

	public record Credentials(String accessKey, String secretKey) {
	}
}
