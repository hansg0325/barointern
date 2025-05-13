package com.example.barointern.presentation.dto;

import com.example.barointern.application.dto.LoginCommand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
	private String username;
	private String password;

	// LoginRequest를 LoginCommand로 변환하는 메서드
	public LoginCommand toLoginCommand() {
		return new LoginCommand(this.username, this.password);
	}
}
