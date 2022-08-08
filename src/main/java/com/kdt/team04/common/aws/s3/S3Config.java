package com.kdt.team04.common.aws.s3;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.kdt.team04.common.aws.config.AwsConfigProperties;

@EnableConfigurationProperties({S3ConfigProperties.class})
public class S3Config {

	public S3Config(AwsConfigProperties awsConfigProperties) {
		this.awsConfigProperties = awsConfigProperties;
	}

	private final AwsConfigProperties awsConfigProperties;

	@Bean(name = "amazonS3")
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
