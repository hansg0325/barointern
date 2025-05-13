package com.example.barointern.application.dto;

import java.util.List;

import com.example.barointern.domain.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 응답 DTO")
public class UserResponse {

	@Schema(description = "사용자 아이디")
	private String username;

	@Schema(description = "닉네임")
	private String nickname;

	@Schema(description = "유저 권한 리스트")
	private List<RoleResponse> roles;

	public UserResponse(User user) {
		this.username = user.getUsername();
		this.nickname = user.getNickname();
		this.roles = user.getRole() != null ?
			List.of(new RoleResponse(user.getRole())) : List.of();
	}
}
