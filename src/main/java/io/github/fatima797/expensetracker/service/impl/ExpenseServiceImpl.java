package io.github.fatima797.expensetracker.service.impl;

import org.springframework.stereotype.Service;

import io.github.fatima797.expensetracker.dto.CreateExpenseRequest;
import io.github.fatima797.expensetracker.dto.ExpenseResponse;
import io.github.fatima797.expensetracker.mapper.ExpenseMapper;
import io.github.fatima797.expensetracker.model.Expense;
import io.github.fatima797.expensetracker.model.User;
import io.github.fatima797.expensetracker.repository.ExpenseRepository;
import io.github.fatima797.expensetracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    public ExpenseResponse createExpense(CreateExpenseRequest request, User authenticatedUser) {

        Expense expense = expenseMapper.toEntity(request, authenticatedUser);

        Expense savedExpense = expenseRepository.save(expense);

        log.info("Expense created with id: {} for user: {}", savedExpense.getPublicId(), authenticatedUser.getEmail());

        return expenseMapper.toResponse(savedExpense);

    }

}
