package com.example.barointern.application.jwt;

import com.example.barointern.domain.entity.User;

public interface JwtTokenGenerator {
	String createJwtToken(User user);
}
