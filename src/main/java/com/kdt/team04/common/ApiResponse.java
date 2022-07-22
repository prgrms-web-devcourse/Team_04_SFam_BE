package com.kdt.team04.common;

public class ApiResponse<T> {
	private T data;

	private ApiResponse() {

	}

	public ApiResponse(T data) {
		this.data = data;
	}

	public T getResponse() { return data;}
}
