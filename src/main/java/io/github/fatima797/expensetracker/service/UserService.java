package io.github.fatima797.expensetracker.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.fatima797.expensetracker.dto.NewUserRegistration;
import io.github.fatima797.expensetracker.dto.UserRegistrationResponse;
import io.github.fatima797.expensetracker.exception.DuplicateEmailException;
import io.github.fatima797.expensetracker.exception.UserNotFoundException;
import io.github.fatima797.expensetracker.model.User;
import io.github.fatima797.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public UserRegistrationResponse createUser(NewUserRegistration userRequest) {

		String email = userRequest.email().trim().toLowerCase();

		if (userRepository.existsByEmail(email)) {
			throw new DuplicateEmailException("Email already exists");
		}

		User newUser = new User();
		newUser.setName(userRequest.name());
		newUser.setEmail(email);
		newUser.setPassword(passwordEncoder.encode(userRequest.password()));
		User saved = userRepository.save(newUser);

		return new UserRegistrationResponse(
				saved.getPublicId(),
				saved.getEmail(),
				saved.getCreatedAt());
	}

	@Transactional(readOnly = true)
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
	}

}
