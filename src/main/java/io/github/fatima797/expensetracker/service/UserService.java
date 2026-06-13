package io.github.fatima797.expensetracker.service;

import io.github.fatima797.expensetracker.dto.NewUserRegistration;
import io.github.fatima797.expensetracker.dto.UserRegistrationResponse;
import io.github.fatima797.expensetracker.model.User;

public interface UserService {
    UserRegistrationResponse createUser(NewUserRegistration newUserRegistration);

    User getUserByEmail(String email);
}
