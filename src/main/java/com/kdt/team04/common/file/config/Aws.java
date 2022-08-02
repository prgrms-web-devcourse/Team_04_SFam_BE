package com.kdt.team04.common.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
@ConfigurationProperties(prefix = "cloud.aws")
public class Aws {
	private Credentials credentials;

	@Value("${cloud.aws.region.static}")
	private String region;

	public Credentials getCredentials() {
		return credentials;
	}

	public String getRegion() {
		return region;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Bean
	public AmazonS3 amazonS3Client() {
		AWSCredentials credentials = new BasicAWSCredentials(
			this.credentials.getAccessKey(),
			this.credentials.getSecretKey()
		);

		return AmazonS3ClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withRegion(region)
			.build();
	}

}
