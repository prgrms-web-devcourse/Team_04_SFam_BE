package com.kdt.team04.common.file;

import java.util.Arrays;

public enum MimeType {
	JPEG("FFD8FF"),
	JPG("FFD8FF"),
	GIF("474946"),
	PSD("384250"),
	PNG("89504E"),
	BMP("424D");

	private String signature;

	MimeType(String signature) {
		this.signature = signature;
	}

	public String getSignature() {
		return signature;
	}

	public int getBytesLengths() {
		return this.signature.length() / 2;
	}

	public static MimeType of(String extension) {
		return Arrays.stream(MimeType.values())
			.filter(value -> value.toString().equals(extension))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("존재하지 않는 확장자입니다."));
	}

}
