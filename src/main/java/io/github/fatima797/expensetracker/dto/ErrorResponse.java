package io.github.fatima797.expensetracker.dto;

import java.util.Map;

public record ErrorResponse(
		int status,
		Map<String, String> errors,
		String timestamp
		) {

}
