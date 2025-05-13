package com.example.barointern.presentation.controller;

import com.example.barointern.domain.entity.Role;
import com.example.barointern.domain.entity.User;
import com.example.barointern.domain.repository.UserRepository;
import com.example.barointern.presentation.dto.LoginRequest;
import com.example.barointern.presentation.dto.SignUpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void cleanDatabase() {
		userRepository.deleteAll();
	}

	@Test
	void 회원가입_성공() throws Exception {
		SignUpRequest request = SignUpRequest.builder()
			.username("JIN HO")
			.password("12341234")
			.nickname("Mentos")
			.build();

		mockMvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username").value("JIN HO"))
			.andExpect(jsonPath("$.nickname").value("Mentos"));
	}

	@Test
	void 회원가입_실패_이미_존재하는_사용자() throws Exception {
		// Given - 이미 저장된 사용자
		User existingUser = User.builder()
			.username("JIN HO")
			.password(passwordEncoder.encode("12341234"))
			.nickname("Mentos")
			.role(Role.USER)
			.build();
		userRepository.save(existingUser);

		// When - 동일한 username으로 회원가입 요청
		SignUpRequest duplicateRequest = SignUpRequest.builder()
			.username("JIN HO")
			.password("anotherPassword")
			.nickname("anotherNick")
			.build();

		// Then - 400 에러와 에러 메시지 확인
		mockMvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(duplicateRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.code").value("USER_ALREADY_EXISTS"))
			.andExpect(jsonPath("$.error.message").value("이미 가입된 사용자입니다."));
	}

	@Test
	void 로그인_성공() throws Exception {
		// given - 테스트용 유저 저장
		userRepository.save(User.builder()
			.username("JIN HO")
			.password(passwordEncoder.encode("12341234")) // 인코딩 필수
			.nickname("Mentos")
			.role(Role.USER)
			.build());

		LoginRequest request = LoginRequest.builder()
			.username("JIN HO")
			.password("12341234")
			.build();

		String requestBody = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").exists())
			.andExpect(jsonPath("$.token").isString());
	}

	@Test
	void 로그인_실패_아이디_오류() throws Exception {
		// given - 테스트용 유저 저장
		userRepository.save(User.builder()
			.username("JIN HO")
			.password(passwordEncoder.encode("12341234")) // 인코딩 필수
			.nickname("Mentos")
			.role(Role.USER)
			.build());

		LoginRequest request = LoginRequest.builder()
			.username("JINHO")
			.password("12341234")
			.build();

		String requestBody = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isUnauthorized()) // 상태 코드를 401로 변경
			.andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"))
			.andExpect(jsonPath("$.error.message").value("아이디 또는 비밀번호가 올바르지 않습니다."));
	}

	@Test
	void 로그인_실패_비밀번호_오류() throws Exception {
		// given - 테스트용 유저 저장
		userRepository.save(User.builder()
			.username("JIN HO")
			.password(passwordEncoder.encode("12341234")) // 인코딩 필수
			.nickname("Mentos")
			.role(Role.USER)
			.build());

		LoginRequest request = LoginRequest.builder()
			.username("JIN HO")
			.password("1234")
			.build();

		String requestBody = objectMapper.writeValueAsString(request);

		// when & then
		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isUnauthorized()) // 상태 코드를 401로 변경
			.andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"))
			.andExpect(jsonPath("$.error.message").value("아이디 또는 비밀번호가 올바르지 않습니다."));
	}

	@Test
	void 관리자_권한_부여_성공() throws Exception {
		// given - 관리자 계정 저장
		userRepository.save(User.builder()
			.username("admin")
			.password(passwordEncoder.encode("admin123"))
			.nickname("Admin User")
			.role(Role.ADMIN) // 관리자 권한 부여
			.build());

		// given - 일반 사용자 계정 저장
		User user = userRepository.save(User.builder()
			.username("JIN HO")
			.password(passwordEncoder.encode("12341234"))
			.nickname("Mentos")
			.role(Role.USER)
			.build());

		// given - 관리자 권한을 부여할 URL
		String url = "/admin/users/" + user.getId() + "/roles";

		// when & then
		mockMvc.perform(patch(url)
				.with(user("admin").roles(Role.ADMIN.name())) // 관리자로 로그인, 권한 설정
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username").value("JIN HO"))
			.andExpect(jsonPath("$.nickname").value("Mentos"))
			.andExpect(jsonPath("$.roles[0].role").value("ADMIN"));
	}

	@Test
	void 권한이_부족한_경우_접근_제한() throws Exception {
		// given - 일반 사용자 계정 저장
		User user = userRepository.save(User.builder()
			.username("JIN HO")
			.password(passwordEncoder.encode("12341234"))
			.nickname("Mentos")
			.role(Role.USER) // 일반 사용자 권한
			.build());

		// given - 관리자 권한을 부여할 URL
		String url = "/admin/users/" + user.getId() + "/roles";

		// when & then
		mockMvc.perform(patch(url)
				.with(user("JIN HO").roles("USER")) // 일반 사용자로 로그인
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden()) // 접근이 금지되었음을 확인
			.andExpect(jsonPath("$.error.code").value("ACCESS_DENIED")) // error 코드 확인
			.andExpect(jsonPath("$.error.message").value("접근 권한이 없습니다.")); // error 메시지 확인
	}

	@Test
	void 관리자_권한_실패_존재하지_않는_사용자_에게_부여() throws Exception {
		// given - 관리자 계정 저장
		userRepository.save(User.builder()
			.username("admin")
			.password(passwordEncoder.encode("admin123"))
			.nickname("Admin User")
			.role(Role.ADMIN) // 관리자 권한 부여
			.build());

		// given - 일반 사용자 계정 저장
		User user = userRepository.save(User.builder()
			.username("JIN HO")
			.password(passwordEncoder.encode("12341234"))
			.nickname("Mentos")
			.role(Role.USER)
			.build());

		// given - 관리자 권한을 부여할 URL
		String url = "/admin/users/" + 2 + "/roles";

		// when & then
		mockMvc.perform(patch(url)
				.with(user("admin").roles(Role.ADMIN.name())) // 관리자로 로그인, 권한 설정
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.error.code").value("USER_NOT_FOUND")) // error 코드 확인
			.andExpect(jsonPath("$.error.message").value("사용자를 찾을 수 없습니다.")); // error 메시지 확인
	}

	@Test
	void 유효하지_않은_토큰_사용시_접근_거부() throws Exception {
		// given
		String invalidToken = "Bearer invalid.jwt.token";

		// when & then
		mockMvc.perform(get("/admin/users/1/roles")
				.header("Authorization", invalidToken))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.error.code").value("INVALID_TOKEN"))
			.andExpect(jsonPath("$.error.message").value("유효하지 않은 인증 토큰입니다."));
	}






}
