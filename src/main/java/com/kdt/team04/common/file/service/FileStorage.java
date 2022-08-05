package com.kdt.team04.common.file.service;

import org.springframework.core.io.Resource;

public interface FileStorage {

	String uploadByPath(Resource resource, String path);

	void uploadByKey(Resource resource, String key);

	void delete(String key);

}
