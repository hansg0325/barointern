package com.example.barointern.domain.repository;

import java.util.Optional;
import com.example.barointern.domain.entity.User;

public interface UserRepository {
	Optional<User> findById(Long id);
	Optional<User> findByUsername(String username);
	Optional<User> findByNickname(String nickname);
	boolean existsByUsername(String username);
	boolean existsByNickname(String nickname);
	User save(User user);
	void deleteAll();
}
