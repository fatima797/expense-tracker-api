package io.github.fatima797.expensetracker.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.fatima797.expensetracker.model.Expense;
import io.github.fatima797.expensetracker.model.User;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findByPublicIdAndUser(UUID publicId, User user);

}
