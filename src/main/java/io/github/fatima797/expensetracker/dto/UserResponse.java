package io.github.fatima797.expensetracker.dto;

import java.util.UUID;

public record UserResponse(
        String name,
        String email,
        UUID publicId) {

}
