package com.example.barointern.infrastructure.security;

import com.example.barointern.application.jwt.JwtTokenGenerator;
import com.example.barointern.domain.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider implements JwtTokenGenerator {

	@Value("${jwt.secretKey}")  // application.properties에서 비밀 키를 불러옵니다.
	private String secretKeyValue;

	private SecretKey secretKey;

	private static final long EXPIRATION_TIME = 2 * 60 * 60 * 1000L; // 2시간 (밀리초 단위)

	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKeyValue));
	}

	// JWT 토큰 생성
	@Override
	public String createJwtToken(User user) {
		return Jwts.builder()
			.claim("username", user.getUsername())  // 사용자 이름 (sub)
			.claim("role", user.getRole().name())  // 사용자 역할 (roles)
			.issuedAt(new Date(System.currentTimeMillis()))  // 발급 시간 (iat)
			.expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // 만료 시간 (exp)
			.signWith(secretKey, Jwts.SIG.HS512)  // 비밀 키로 서명
			.compact();  // JWT 토큰 생성
	}

	// JWT 토큰에서 payload 추출
	public Claims getClaimsFromToken(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	// JWT 토큰에서 사용자 ID(username) 추출
	public String getUsernameFromToken(String token) {
		return getClaimsFromToken(token).get("username", String.class);
	}

	public String extractUserRole(String token) {
		return getClaimsFromToken(token).get("role", String.class);
	}


	// JWT 토큰 유효성 검사
	public boolean isTokenExpired(String token) {
		return getClaimsFromToken(token).getExpiration().before(new Date());
	}

	// JWT 토큰 검증
	public boolean validateToken(String token, User user) {
		String username = getUsernameFromToken(token);
		return (username.equals(user.getUsername()) && !isTokenExpired(token));
	}
}
