package io.github.fatima797.expensetracker.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserRegistrationResponse(
		UUID publicId,
		String email,
		LocalDateTime createdAt) {

}
