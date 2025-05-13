package com.example.barointern.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(7);

		try {
			Claims claims = jwtTokenProvider.getClaimsFromToken(token);

			String username = claims.get("username", String.class);
			String role = claims.get("role", String.class);

			List<SimpleGrantedAuthority> authorities =
				List.of(new SimpleGrantedAuthority("ROLE_" + role));

			UserDetails userDetails = new org.springframework.security.core.userdetails.User(
				username,
				"", // 비밀번호는 필요 없음
				authorities
			);

			UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(userDetails, null, authorities);


			SecurityContextHolder.getContext().setAuthentication(authenticationToken);

			filterChain.doFilter(request, response);

		} catch (JwtException | IllegalArgumentException e) {
			// 유효하지 않은 토큰
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
				"INVALID_TOKEN", "유효하지 않은 인증 토큰입니다.");
		}
	}

	private void sendErrorResponse(HttpServletResponse response, int status,
		String errorCode, String message) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json;charset=UTF-8");

		String body = String.format("""
			{
			  "error": {
			    "code": "%s",
			    "message": "%s"
			  }
			}
			""", errorCode, message);

		response.getWriter().write(body);
	}
}
