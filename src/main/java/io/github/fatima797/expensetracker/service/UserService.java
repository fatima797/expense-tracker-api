package io.github.fatima797.expensetracker.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.fatima797.expensetracker.dto.UserRequest;
import io.github.fatima797.expensetracker.dto.UserResponse;
import io.github.fatima797.expensetracker.model.User;
import io.github.fatima797.expensetracker.repository.UserRepository;
import jakarta.transaction.Transactional;

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
			throw new IllegalArgumentException("Email already in use");
			//TODO : Custom Exception handling
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

}
