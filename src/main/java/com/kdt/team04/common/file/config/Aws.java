package com.kdt.team04.common.file.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Profile({"dev", "real"})
@Configuration
@EnableConfigurationProperties({AwsConfigProperties.class, S3ConfigProperties.class})
public class Aws {

	private final AwsConfigProperties awsConfigProperties;

	public Aws(AwsConfigProperties awsConfigProperties) {
		this.awsConfigProperties = awsConfigProperties;
	}

	@Bean
	public AmazonS3 amazonS3Client() {
		AWSCredentials credentials = new BasicAWSCredentials(
			this.awsConfigProperties.credentials().accessKey(),
			this.awsConfigProperties.credentials().secretKey()
		);

		return AmazonS3ClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withRegion(awsConfigProperties.region())
			.build();
	}

}
