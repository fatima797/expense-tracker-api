package io.github.fatima797.expensetracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NewUserRegistration(
		@NotBlank(message = "Name is required") @Size(min = 3, max = 20) String name,

		@NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email,

		@NotBlank(message = "Password is required") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character") String password) {

}
