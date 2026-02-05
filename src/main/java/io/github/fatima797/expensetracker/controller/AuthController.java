package io.github.fatima797.expensetracker.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.fatima797.expensetracker.dto.UserRequest;
import io.github.fatima797.expensetracker.dto.UserResponse;
import io.github.fatima797.expensetracker.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	
	private final UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest userRequest) {
        UserResponse response = userService.createUser(userRequest);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/api/v1/users/{publicId}") 
                .buildAndExpand(response.publicId())
                .toUri();
        
        return ResponseEntity.created(location).body(response);
    }

}
