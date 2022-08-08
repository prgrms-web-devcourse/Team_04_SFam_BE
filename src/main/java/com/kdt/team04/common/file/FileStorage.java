package com.kdt.team04.common.file;

import org.springframework.core.io.Resource;

public interface FileStorage {

	String uploadByPath(Resource resource, Path path);

	void uploadByKey(Resource resource, String key);

	void delete(String key);

}
