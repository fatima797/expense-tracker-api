package io.github.fatima797.expensetracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.fatima797.expensetracker.dto.UserResponse;
import io.github.fatima797.expensetracker.model.User;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserResponse> currentUserName(@AuthenticationPrincipal User user) {
        UserResponse userResponse = new UserResponse(user.getName(), user.getEmail(), user.getPublicId());
        return ResponseEntity.ok(userResponse);
    }

}
