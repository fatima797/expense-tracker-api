package io.github.fatima797.expensetracker.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.fatima797.expensetracker.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	Optional<User> findByPublicId(UUID publicId);
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);

}
