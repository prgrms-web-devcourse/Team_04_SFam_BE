package com.kdt.team04.common.file.service;

import org.springframework.core.io.Resource;

import com.kdt.team04.common.file.ImagePath;

public interface FileStorage {

	String upload(Resource resource, ImagePath path);

	void delete(String key);

}
