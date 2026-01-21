package io.github.fatima797.expensetracker;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.github.fatima797.expensetracker.model.Expense;
import io.github.fatima797.expensetracker.model.ExpenseCategory;
import io.github.fatima797.expensetracker.model.User;
import io.github.fatima797.expensetracker.repository.ExpenseRepository;
import io.github.fatima797.expensetracker.repository.UserRepository;

@SpringBootApplication
public class ExpenseTrackerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpenseTrackerApiApplication.class, args);


	}
	
	@Bean
	CommandLineRunner seedUserAndExpense(UserRepository userRepository, ExpenseRepository expenseRepository) {
		return args -> {
			String email = "test@example.com";

			if (!userRepository.existsByEmail(email)) {
				User user1 = new User("testUser", email, "Password123!");
				userRepository.save(user1);
				
				Expense expense1 = new Expense("Groceries", "Grocery shopping", 
						new BigDecimal("95.50"), 
						LocalDate.now().minusDays(2), 
						ExpenseCategory.GROCERIES, user1);
				
				expenseRepository.save(expense1);
				
				Expense expense2 = new Expense("Bill", "Electricity bill", 
						new BigDecimal("60.00"), 
						LocalDate.now(), 
						ExpenseCategory.UTILITIES, user1);
				
				expenseRepository.save(expense2);
				
				System.out.println("Seeded user: " + user1.getEmail() + " with ID: " + user1.getId());
				System.out.println("Seeded expense: " + expense1.getName() + " with ID: " + expense1.getId());
				System.out.println("Seeded expense: " + expense2.getName() + " with ID: " + expense2.getId());
			} else {
				System.out.println("User already exists, skipping seed.");
			}

		};

	}
}
