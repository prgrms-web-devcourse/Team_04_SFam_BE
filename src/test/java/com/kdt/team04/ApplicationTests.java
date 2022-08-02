package com.kdt.team04;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.aws.autoconfigure.context.ContextRegionProviderAutoConfiguration;

import com.amazonaws.services.s3.AmazonS3;
import com.kdt.team04.common.file.service.S3Uploader;

@SpringBootTest
@EnableAutoConfiguration(exclude = ContextRegionProviderAutoConfiguration.class)
class ApplicationTests {

	@MockBean
	S3Uploader s3Uploader;

	@MockBean
	AmazonS3 amazonS3;

	@Test
	void contextLoads() {
	}

}
