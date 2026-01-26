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
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}

	@PostMapping
	public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest userRequest) {
		UserResponse response = userService.createUser(userRequest);
		
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{publicId}")
				.buildAndExpand(response.publicId())
				.toUri();

		return ResponseEntity.created(location).body(response);

	}
}
