package io.github.fatima797.expensetracker.controller;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.fatima797.expensetracker.dto.LoginRequest;
import io.github.fatima797.expensetracker.dto.NewUserRegistration;
import io.github.fatima797.expensetracker.model.User;
import io.github.fatima797.expensetracker.repository.UserRepository;
import io.github.fatima797.expensetracker.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository repository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@AfterEach
	void tearDown() {
		repository.deleteAll();
	}

	@Test
	void register_WithValidCredentials_ShouldReturn201Created() throws Exception {
		NewUserRegistration request = new NewUserRegistration("test123", "test@example.com", "Password123!");

		mockMvc.perform(post("/api/v1/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(header().exists("Location"))
				.andExpect(jsonPath("$.publicId").exists())
				.andExpect(jsonPath("$.email").value("test@example.com"))
				.andExpect(jsonPath("$.createdAt").exists());

	}

	@Test
	void register_WithExistingEmail_ShouldReturn409Conflict() throws Exception {

		NewUserRegistration initialRequest = new NewUserRegistration("originalUser", "duplicate@example.com",
				"Password123!");

		mockMvc.perform(post("/api/v1/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(initialRequest)))
				.andExpect(status().isCreated());

		NewUserRegistration duplicateRequest = new NewUserRegistration("secondUser", "duplicate@example.com",
				"DifferentPass123!");

		mockMvc.perform(post("/api/v1/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(duplicateRequest)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.status").value(409))
				.andExpect(jsonPath("$.errors.email").value("Email already exists"))
				.andExpect(jsonPath("$.timestamp").exists());

	}

	@Test
	void register_ShouldSaveHashedPasswordInDatabase() throws Exception {

		String rawPassword = "Password123!";
		NewUserRegistration request = new NewUserRegistration("secureUser", "hash@example.com", rawPassword);

		mockMvc.perform(post("/api/v1/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated());

		User savedUser = repository.findByEmail("hash@example.com")
				.orElseThrow(() -> new AssertionError("User was not saved in the database"));

		assertNotEquals(rawPassword, savedUser.getPassword());
		assertTrue(savedUser.getPassword().startsWith("$2")); // Using BCrypt hashing, which starts with $2a$, $2b$,
																// or $2y$
		assertTrue(passwordEncoder.matches(rawPassword, savedUser.getPassword()),
				"The raw password should match the hashed password in the database");
	}

	@Test
	void login_WithWrongPassword_ShouldReturn401Unauthorized() throws Exception {

		userService.createUser(new NewUserRegistration("testuser", "test@example.com", "Password123!"));

		LoginRequest badRequest = new LoginRequest("test@example.com", "WrongPassword!");

		mockMvc.perform(post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(badRequest)))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.status").value(401))
				.andExpect(jsonPath("$.errors.credentials").value("Invalid email or password"))
				.andExpect(jsonPath("$.timestamp").exists());

	}

	@Test
	void login_WithNonExistentEmail_ShouldReturn401Unauthorized() throws Exception {
		LoginRequest request = new LoginRequest("ghost@example.com", "Password123!");

		mockMvc.perform(post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.errors.credentials").value("Invalid email or password"))
				.andExpect(jsonPath("$.timestamp").exists())
				.andExpect(jsonPath("$.errors.user").doesNotExist());
	}

	@Test
	void login_WithInvalidEmailFormat_ShouldReturn400BadRequest() throws Exception {

		LoginRequest malformedRequest = new LoginRequest("not-an-email", "Password123!");

		mockMvc.perform(post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(malformedRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.errors.email").value("Email is not valid"))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	void login_WithValidCredentials_ShouldReturn200OK() throws Exception {

		NewUserRegistration registerRequest = new NewUserRegistration("testuser", "test@example.com", "Password123!");
		userService.createUser(registerRequest);

		LoginRequest loginRequest = new LoginRequest("test@example.com", "Password123!");

		mockMvc.perform(post("/api/v1/auth/login")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists())
				.andExpect(jsonPath("$.email").value("test@example.com"))
				.andExpect(jsonPath("$.publicId").exists());

	}

	@Test
	void login_WithBlankFields_ShouldReturn400BadRequest() throws Exception {
		LoginRequest blankRequest = new LoginRequest("", "");

		mockMvc.perform(post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(blankRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.errors.email").exists())
				.andExpect(jsonPath("$.errors.password").exists());
	}

}
