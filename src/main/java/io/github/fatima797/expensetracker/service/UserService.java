package io.github.fatima797.expensetracker.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.fatima797.expensetracker.dto.UserRequest;
import io.github.fatima797.expensetracker.dto.UserResponse;
import io.github.fatima797.expensetracker.exception.DuplicateEmailException;
import io.github.fatima797.expensetracker.exception.UserNotFoundException;
import io.github.fatima797.expensetracker.model.User;
import io.github.fatima797.expensetracker.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional
	public UserResponse createUser(UserRequest userRequest) {
		
		String email = userRequest.email().trim().toLowerCase();
		
		if(userRepository.existsByEmail(email)) {
			throw new DuplicateEmailException("Email already exists");
		}  
		
		User newUser = new User(
				userRequest.username(), 
				userRequest.email(), 
				passwordEncoder.encode(userRequest.password())
				);
		User saved = userRepository.save(newUser);
		
		return new UserResponse(
				saved.getPublicId(),
				saved.getEmail(),
				saved.getCreatedAt()
				);
	}

	@Transactional(readOnly = true)
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
	}

}
