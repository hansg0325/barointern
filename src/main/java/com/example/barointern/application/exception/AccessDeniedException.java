package com.example.barointern.application.exception;

public class AccessDeniedException extends RuntimeException {
	public AccessDeniedException(String message) {
		super(message);
	}
}
