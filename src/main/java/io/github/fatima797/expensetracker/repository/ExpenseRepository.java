package io.github.fatima797.expensetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.fatima797.expensetracker.model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

}
