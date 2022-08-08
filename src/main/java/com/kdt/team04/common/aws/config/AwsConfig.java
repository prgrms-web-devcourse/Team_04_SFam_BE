package com.kdt.team04.common.aws.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.kdt.team04.common.aws.s3.S3Config;

@Profile({"local", "dev", "real"})
@Configuration
@EnableConfigurationProperties({AwsConfigProperties.class})
@Import(S3Config.class)
public class AwsConfig {

}
