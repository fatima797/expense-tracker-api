package io.github.fatima797.expensetracker.dto;

public record SecurityErrorResponse(
        int status,
        String error,
        String message) {

}
