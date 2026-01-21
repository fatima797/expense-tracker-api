package io.github.fatima797.expensetracker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import io.github.fatima797.expensetracker.model.User;
import io.github.fatima797.expensetracker.repository.UserRepository;

@SpringBootApplication
public class ExpenseTrackerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpenseTrackerApiApplication.class, args);


	}

	@Bean
	CommandLineRunner seedUser(UserRepository userRepository) {
		return args -> {
			String email = "test@example.com";

			if (!userRepository.existsByEmail(email)) {
				User user = new User("testUser", email, "Password123!");

				userRepository.save(user);
				
				System.out.println("Seeded user: " + user.getEmail() + " with ID: " + user.getId());
			} else {
				System.out.println("User already exists, skipping seed.");
			}

		};

	}

}
