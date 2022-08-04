package com.kdt.team04.common.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "cloud.aws.s3")
public record S3ConfigProperties(String bucket, String url) {
}

