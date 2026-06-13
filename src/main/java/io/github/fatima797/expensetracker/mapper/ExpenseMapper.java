package io.github.fatima797.expensetracker.mapper;

import org.springframework.stereotype.Component;

import io.github.fatima797.expensetracker.dto.CreateExpenseRequest;
import io.github.fatima797.expensetracker.dto.ExpenseResponse;
import io.github.fatima797.expensetracker.model.Expense;
import io.github.fatima797.expensetracker.model.User;

@Component
public class ExpenseMapper {

    public Expense toEntity(CreateExpenseRequest request, User authenticatedUser) {
        Expense expense = new Expense();
        expense.setUser(authenticatedUser);

        expense.setDescription(request.description());
        expense.setAmount(request.amount());
        expense.setCategory(request.category());
        expense.setDate(request.date());

        return expense;
    }

    public ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getPublicId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDate());
    }

}
