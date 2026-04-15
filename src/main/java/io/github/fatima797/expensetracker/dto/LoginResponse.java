package io.github.fatima797.expensetracker.dto;

public record LoginResponse(
		String token,
		String username,
		String publicId
		) {

}
