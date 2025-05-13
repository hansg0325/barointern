package com.example.barointern.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.example.barointern.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "유저 권한 DTO")
public class RoleResponse {

	@Schema(description = "역할 이름 (USER or ADMIN)")
	private String role;

	public RoleResponse(Role role) {
		this.role = role.name();
	}
}
