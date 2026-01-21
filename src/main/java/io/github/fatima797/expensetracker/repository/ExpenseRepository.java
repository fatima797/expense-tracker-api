package io.github.fatima797.expensetracker.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.fatima797.expensetracker.model.Expense;
import io.github.fatima797.expensetracker.model.ExpenseCategory;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
	List<Expense> findByUserId(Long userId);

	List<Expense> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

	List<Expense> findByUserIdAndCategory(Long userId, ExpenseCategory category);

}
