package com.example.barointern.presentation.controller;

import com.example.barointern.application.dto.LoginResponse;
import com.example.barointern.application.service.UserService;
import com.example.barointern.presentation.dto.LoginRequest;
import com.example.barointern.presentation.dto.SignUpRequest;
import com.example.barointern.application.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 관련 API")
public class UserController {

	private final UserService userService;

	@Operation(summary = "회원가입", description = "사용자가 회원가입을 진행합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "회원가입 성공",
			content = @Content(schema = @Schema(implementation = UserResponse.class))),
		@ApiResponse(responseCode = "400", description = "회원가입 실패 (이미 가입된 사용자)",
			content = @Content(schema = @Schema(example = "{ \"error\": { \"code\": \"string\", \"message\": \"string\" } }")))
	})
	@PostMapping("/signup")
	public ResponseEntity<UserResponse> signUp(@RequestBody SignUpRequest request) {
		UserResponse response = userService.signUp(request.toSignUpCommand());
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "로그인", description = "사용자가 로그인을 수행하고 토큰을 발급받습니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그인 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패 (아이디/비밀번호 오류)")
	})
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
		LoginResponse response = userService.login(request.toLoginCommand());
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "관리자 권한 부여", description = "ADMIN 권한을 가진 사용자가 다른 유저에게 관리자 권한을 부여합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "권한 부여 성공"),
		@ApiResponse(responseCode = "403", description = "접근 거부 (권한 없음)"),
		@ApiResponse(responseCode = "404", description = "사용자 찾을 수 없음")
	})
	@PatchMapping("/admin/users/{userId}/roles")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserResponse> grantAdminRole(@PathVariable Long userId) {
		UserResponse response = userService.grantAdminRole(userId);
		return ResponseEntity.ok(response);
	}
}
