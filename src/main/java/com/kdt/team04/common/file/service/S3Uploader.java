package com.kdt.team04.common.file.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.ErrorCode;
import com.kdt.team04.common.file.FileValidator;
import com.kdt.team04.common.file.config.S3ConfigProperties;

@Component
@Profile({"dev", "real"})
public class S3Uploader implements FileStorage {

	@Qualifier(value = "amazonS3")
	private final AmazonS3 amazonS3;
	private final S3ConfigProperties s3;
	private final FileValidator fileValidator;

	public S3Uploader(AmazonS3 amazonS3, FileValidator fileValidator, S3ConfigProperties s3) {
		this.amazonS3 = amazonS3;
		this.fileValidator = fileValidator;
		this.s3 = s3;
	}

	@Override
	public String uploadByPath(Resource resource, String path) {
		fileValidator.validate(resource);

		String extension = fileValidator.getExtension(resource.getFilename());
		String key = path + UUID.randomUUID() + "." + extension;
		write(resource, key);

		return s3.url() + key;
	}

	@Override
	public void uploadByKey(Resource resource, String key) {
		fileValidator.validate(resource);

		String originalKey = key.substring(s3.url().length());
		write(resource, originalKey);
	}

	private void write(Resource resource, String key) {
		try (InputStream inputStream = resource.getInputStream()) {
			ObjectMetadata metadata = new ObjectMetadata();

			metadata.setContentType(
				URLConnection.guessContentTypeFromName(resource.getFilename())
			);
			metadata.setContentLength(resource.contentLength());

			amazonS3.putObject(
				new PutObjectRequest(
					s3.bucket(),
					key,
					inputStream,
					metadata)
					.withCannedAcl(CannedAccessControlList.PublicRead)
			);
		} catch (IOException e) {
			throw new BusinessException(ErrorCode.FILE_IO,
				MessageFormat.format("{0} 파일을 읽는 작업에 실패했습니다.", resource.getFilename()));
		} catch (AmazonClientException e) {
			throw new BusinessException(ErrorCode.INTERNAL_SEVER_ERROR,
				"AWS S3에 저장하는 작업에 실패했습니다.");
		}
	}

	@Override
	public void delete(String key) {
		try {
			amazonS3.deleteObject(
				new DeleteObjectRequest(
					s3.bucket(),
					key.substring(s3.url().length())
				)
			);
		} catch (AmazonClientException e) {
			throw new BusinessException(ErrorCode.INTERNAL_SEVER_ERROR,
				"AWS S3의 파일을 삭제하는 작업에 실패했습니다.");
		}
	}

}
