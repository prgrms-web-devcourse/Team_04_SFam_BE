package com.kdt.team04.common.file;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.kdt.team04.common.exception.BusinessException;
import com.kdt.team04.common.exception.ErrorCode;

@Component
public class FileValidator {

	public boolean validate(Resource resource) {
		String extension = getExtension(resource.getFilename());
		MimeType type = MimeType.of(extension);

		try(InputStream inputStream = resource.getInputStream()) {
			byte[] bytes = new byte[type.getBytesLengths()];
			inputStream.read(bytes);

			String hexString = byteToHexString(bytes);
			if (!type.getSignature().equals(hexString)) {
				throw new BusinessException(ErrorCode.INVALID_FILE_SIGNATURE,
					MessageFormat.format("{0} 확장자 파일의 시그니처가 올바르지 않습니다.", extension));
			}
		} catch (IOException e) {
			throw new BusinessException(ErrorCode.FILE_IO,
				MessageFormat.format("{0} 파일을 읽어오는데 문제가 발생했습니다.", resource.getFilename()));
		}

		return true;
	}
	public String getExtension(String fileName) {
		return fileName
			.substring(fileName.indexOf(".") + 1)
			.toUpperCase();
	}

	private String byteToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte data : bytes) {
			sb.append(Integer.toString((data & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString().toUpperCase();
	}

}
