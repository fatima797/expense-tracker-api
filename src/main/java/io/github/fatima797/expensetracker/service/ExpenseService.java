package io.github.fatima797.expensetracker.service;

import io.github.fatima797.expensetracker.dto.CreateExpenseRequest;
import io.github.fatima797.expensetracker.model.User;
import io.github.fatima797.expensetracker.dto.ExpenseResponse;

public interface ExpenseService {

    ExpenseResponse createExpense(CreateExpenseRequest request, User authenticatedUser);
}
