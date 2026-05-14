package io.github.fatima797.expensetracker.controller.auth;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.fatima797.expensetracker.dto.LoginRequest;
import io.github.fatima797.expensetracker.dto.LoginResponse;
import io.github.fatima797.expensetracker.dto.NewUserRegistration;
import io.github.fatima797.expensetracker.dto.UserRegistrationResponse;
import io.github.fatima797.expensetracker.service.JwtService;
import io.github.fatima797.expensetracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;

	private final AuthenticationManager authenticationManager;

	private final JwtService jwtService;

	@PostMapping("/register")
	public ResponseEntity<UserRegistrationResponse> register(@Valid @RequestBody NewUserRegistration userRequest) {
		UserRegistrationResponse response = userService.createUser(userRequest);

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/api/v1/users/{publicId}")
				.buildAndExpand(response.publicId())
				.toUri();

		return ResponseEntity.created(location).body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

		var user = userService.getUserByEmail(request.email());
		String jwtToken = jwtService.generateToken(user);

		return ResponseEntity.ok(new LoginResponse(
				jwtToken,
				user.getEmail(),
				user.getPublicId().toString()));

	}

}
