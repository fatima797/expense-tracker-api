package io.github.fatima797.expensetracker.service;

import org.springframework.stereotype.Service;

import io.github.fatima797.expensetracker.dto.UserRequest;
import io.github.fatima797.expensetracker.dto.UserResponse;
import io.github.fatima797.expensetracker.model.User;
import io.github.fatima797.expensetracker.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserService {
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
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
				userRequest.password() // TODO: Hash password before saving
				);
		User saved = userRepository.save(newUser);
		
		return new UserResponse(
				saved.getPublicId(),
				saved.getUsername(),
				saved.getCreatedAt()
				);
	}

}
