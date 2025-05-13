package com.example.barointern.presentation.dto;

import com.example.barointern.application.dto.SignUpCommand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
	private String username;
	private String password;
	private String nickname;

	// SignUpRequest를 SignUpCommand로 변환하는 메서드
	public SignUpCommand toSignUpCommand() {
		return new SignUpCommand(this.username, this.password, this.nickname);
	}
}
