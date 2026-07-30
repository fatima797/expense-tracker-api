package io.github.fatima797.expensetracker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.fatima797.expensetracker.dto.CreateExpenseRequest;
import io.github.fatima797.expensetracker.dto.ExpenseResponse;
import io.github.fatima797.expensetracker.exception.ExpenseNotFoundException;
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
        UUID publicId = UUID.randomUUID();
        LocalDate today = LocalDate.now();
        String description = "Bought eggs and milk";
        BigDecimal amount = new BigDecimal("25.95");
        ExpenseCategory category = ExpenseCategory.GROCERIES;

        User testUser = new User();
        testUser.setName("test");
        testUser.setEmail("test@example.com");

        CreateExpenseRequest request = new CreateExpenseRequest(
                description, amount, category, today);

        Expense mockEntity = new Expense();
        ExpenseResponse expectedResponse = new ExpenseResponse(
                publicId, description, amount, category, today);

        when(expenseMapper.toEntity(request, testUser)).thenReturn(mockEntity);
        when(expenseRepository.save(mockEntity)).thenReturn(mockEntity);
        when(expenseMapper.toResponse(mockEntity)).thenReturn(expectedResponse);

        ExpenseResponse result = expenseService.createExpense(request, testUser);

        assertEquals(expectedResponse, result);
        verify(expenseMapper).toEntity(request, testUser);
        verify(expenseRepository).save(mockEntity);
        verify(expenseMapper).toResponse(mockEntity);
    }

    @Test
    void getExpense_ShouldReturnExpenseResponse_WhenExpenseExists() {
        UUID publicId = UUID.randomUUID();
        LocalDate today = LocalDate.now();

        User testUser = new User();
        testUser.setName("test");
        testUser.setEmail("test@example.com");

        Expense mockEntity = new Expense();
        ExpenseResponse expectedResponse = new ExpenseResponse(
                publicId, "Food", new BigDecimal("10.00"),
                ExpenseCategory.GROCERIES, today);

        when(expenseRepository.findByPublicIdAndUser(publicId, testUser))
                .thenReturn(Optional.of(mockEntity));
        when(expenseMapper.toResponse(mockEntity)).thenReturn(expectedResponse);

        ExpenseResponse result = expenseService.getExpense(publicId, testUser);

        assertEquals(expectedResponse, result);
        verify(expenseRepository).findByPublicIdAndUser(publicId, testUser);
        verify(expenseMapper).toResponse(mockEntity);
    }

    @Test
    void getExpense_ShouldThrowExpenseNotFoundException_WhenExpenseDoesNotExist() {
        UUID publicId = UUID.randomUUID();
        User testUser = new User();

        when(expenseRepository.findByPublicIdAndUser(publicId, testUser)).thenReturn(Optional.empty());

        assertThrows(ExpenseNotFoundException.class, () -> expenseService.getExpense(publicId, testUser));

        verify(expenseRepository).findByPublicIdAndUser(publicId, testUser);
        verify(expenseMapper, never()).toResponse(any());

    }

}
