package io.github.fatima797.expensetracker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.fatima797.expensetracker.dto.CreateExpenseRequest;
import io.github.fatima797.expensetracker.dto.ExpenseResponse;
import io.github.fatima797.expensetracker.mapper.ExpenseMapper;
import io.github.fatima797.expensetracker.model.Expense;
import io.github.fatima797.expensetracker.model.ExpenseCategory;
import io.github.fatima797.expensetracker.model.User;
import io.github.fatima797.expensetracker.repository.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @Test
    void createExpense_ShouldReturnExpenseResponse() {
        User testUser = new User();
        testUser.setName("test");
        testUser.setEmail("test@example.com");

        String description = "Bought eggs and milk";
        BigDecimal amount = new BigDecimal("25.95");
        ExpenseCategory category = ExpenseCategory.GROCERIES;
        LocalDate today = LocalDate.now();

        CreateExpenseRequest validRequest = new CreateExpenseRequest(description, amount, category, today);

        Expense expense = new Expense();
        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setDate(today);

        ExpenseResponse expectedResponse = new ExpenseResponse(
                UUID.randomUUID(),
                description,
                amount,
                category,
                today);

        when(expenseMapper.toEntity(validRequest, testUser)).thenReturn(expense);
        when(expenseRepository.save(expense)).thenReturn(expense);
        when(expenseMapper.toResponse(expense)).thenReturn(expectedResponse);

        ExpenseResponse actualResponse = expenseService.createExpense(validRequest, testUser);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.publicId(), actualResponse.publicId());
        assertEquals(expectedResponse.description(), actualResponse.description());
        assertEquals(expectedResponse.amount(), actualResponse.amount());
        assertEquals(expectedResponse.category(), actualResponse.category());
        assertEquals(expectedResponse.date(), actualResponse.date());
    }

}
