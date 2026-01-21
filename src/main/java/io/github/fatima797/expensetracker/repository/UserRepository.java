package io.github.fatima797.expensetracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.fatima797.expensetracker.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);

}
