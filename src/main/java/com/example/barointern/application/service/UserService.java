package com.example.barointern.application.service;

import com.example.barointern.application.dto.LoginCommand;
import com.example.barointern.application.dto.LoginResponse;
import com.example.barointern.application.dto.SignUpCommand;
import com.example.barointern.application.jwt.JwtTokenGenerator;
import com.example.barointern.domain.entity.User;
import com.example.barointern.domain.entity.Role;
import com.example.barointern.application.exception.InvalidCredentialsException;
import com.example.barointern.application.exception.UserNotFoundException;
import com.example.barointern.domain.repository.UserRepository;
import com.example.barointern.application.dto.UserResponse;
import com.example.barointern.application.exception.UserAlreadyExistsException; // 도메인 예외

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenGenerator jwtTokenGenerator;  // 도메인 서비스로 사용

	public UserResponse signUp(SignUpCommand command) {
		if (userRepository.existsByUsername(command.getUsername())) {
			throw new UserAlreadyExistsException("이미 가입된 사용자입니다.");
		}

		if (userRepository.existsByNickname(command.getNickname())) {
			throw new UserAlreadyExistsException("이미 가입된 사용자입니다.");
		}

		User user = User.builder()
			.username(command.getUsername())
			.password(passwordEncoder.encode(command.getPassword()))
			.nickname(command.getNickname())
			.role(Role.USER)
			.build();

		User saved = userRepository.save(user);

		// UserResponse에 User 객체를 넘겨서 생성
		return new UserResponse(saved);
	}

	// 로그인 처리
	public LoginResponse login(LoginCommand command) {
		// 사용자 조회
		User user = userRepository.findByUsername(command.getUsername())
			.orElseThrow(() -> new InvalidCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다."));

		// 비밀번호 매칭 확인
		if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
		}

		// JWT 토큰 생성
		String token = jwtTokenGenerator.createJwtToken(user);


		return new LoginResponse(token);

	}

	public UserResponse grantAdminRole(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

		user.setRole(Role.ADMIN);
		userRepository.save(user);

		return new UserResponse(user);
	}



}
