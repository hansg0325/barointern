package com.example.barointern.infrastructure.repository;

import com.example.barointern.domain.entity.User;
import com.example.barointern.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long>, UserRepository {

	@Override
	Optional<User> findByUsername(String username);

	@Override
	Optional<User> findByNickname(String nickname);

	@Override
	boolean existsByUsername(String username);

	@Override
	boolean existsByNickname(String nickname);
}
