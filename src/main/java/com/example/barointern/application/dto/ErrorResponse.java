package com.example.barointern.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
	private Error error;

	public ErrorResponse(String code, String message) {
		this.error = new Error(code, message);
	}

	@Getter
	@NoArgsConstructor
	public static class Error {
		private String code;
		private String message;

		public Error(String code, String message) {
			this.code = code;
			this.message = message;
		}
	}
}
