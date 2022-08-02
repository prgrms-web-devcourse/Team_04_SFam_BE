package com.kdt.team04.common.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cloud.aws.s3")
public class S3 {
	private String bucket;
	private String url;

	public String getBucket() {
		return bucket;
	}

	public String getUrl() {
		return url;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}

