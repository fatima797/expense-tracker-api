package io.github.fatima797.expensetracker.dto;

public record LoginResponse(
		String token,
		String email,
		String publicId) {

}
