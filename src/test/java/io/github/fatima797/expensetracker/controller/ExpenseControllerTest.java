package io.github.fatima797.expensetracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.fatima797.expensetracker.config.SecurityConfig;
import io.github.fatima797.expensetracker.dto.CreateExpenseRequest;
import io.github.fatima797.expensetracker.dto.ExpenseResponse;
import io.github.fatima797.expensetracker.exception.GlobalExceptionHandler;
import io.github.fatima797.expensetracker.model.ExpenseCategory;
import io.github.fatima797.expensetracker.model.User;
import io.github.fatima797.expensetracker.security.CustomAuthenticationEntryPoint;
import io.github.fatima797.expensetracker.service.ExpenseService;
import io.github.fatima797.expensetracker.service.JwtService;

@WebMvcTest(ExpenseController.class)
@Import({ SecurityConfig.class, JwtService.class, GlobalExceptionHandler.class, CustomAuthenticationEntryPoint.class })
@ActiveProfiles("test")
public class ExpenseControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private ExpenseService expenseService;

        @Autowired
        private JwtService jwtService;

        @MockitoBean
        private UserDetailsService userDetailsService;

        private String validToken;

        @BeforeEach
        void setUp() {
                User testUser = new User();
                testUser.setName("test");
                testUser.setEmail("test@example.com");
                testUser.setPassword("Password123!");

                this.validToken = jwtService.generateToken(testUser);

                Mockito.when(userDetailsService.loadUserByUsername("test@example.com"))
                                .thenReturn(testUser);
        }

        @Test
        void createExpense_ShouldReturn201Created() throws Exception {
                UUID expectedId = UUID.randomUUID();
                String expectedDescription = "Bought eggs and milk";
                BigDecimal expectedAmount = new BigDecimal("15.95");
                LocalDate expectedDate = LocalDate.now().minusDays(5);

                CreateExpenseRequest validExpenseRequest = new CreateExpenseRequest(expectedDescription,
                                expectedAmount,
                                ExpenseCategory.GROCERIES, expectedDate);

                ExpenseResponse mockResponse = new ExpenseResponse(expectedId, expectedDescription, expectedAmount,
                                ExpenseCategory.GROCERIES, expectedDate);

                Mockito.when(expenseService.createExpense(any(CreateExpenseRequest.class), any()))
                                .thenReturn(mockResponse);

                String jsonPayload = objectMapper.writeValueAsString(validExpenseRequest);

                mockMvc.perform(post("/api/v1/expenses")
                                .header("Authorization", "Bearer " + validToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPayload))

                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.publicId").value(expectedId.toString()))
                                .andExpect(jsonPath("$.description").value(expectedDescription))
                                .andExpect(jsonPath("$.amount").value(expectedAmount.doubleValue()))
                                .andExpect(jsonPath("$.category").value(ExpenseCategory.GROCERIES.name()))
                                .andExpect(jsonPath("$.date").value(expectedDate.toString()));

        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("invalidCreateExpenseRequests")
        void createExpense_WithInvalidInput_ShouldReturn400(String displayName, CreateExpenseRequest request,
                        String expectedErrorField) throws Exception {

                String jsonPayload = objectMapper.writeValueAsString(request);

                mockMvc.perform(post("/api/v1/expenses")
                                .header("Authorization", "Bearer " + validToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPayload))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.errors." + expectedErrorField).isNotEmpty());
        }

        private static Stream<Arguments> invalidCreateExpenseRequests() {
                LocalDate validDate = LocalDate.now().minusDays(1);
                LocalDate futureDate = LocalDate.now().plusDays(1);
                String longDescription = "A".repeat(256);

                return Stream.of(
                                Arguments.of("negative amount",
                                                new CreateExpenseRequest("Test", new BigDecimal("-1.00"),
                                                                ExpenseCategory.GROCERIES, validDate),
                                                "amount"),
                                Arguments.of("zero amount",
                                                new CreateExpenseRequest("Test", new BigDecimal("0.00"),
                                                                ExpenseCategory.GROCERIES, validDate),
                                                "amount"),
                                Arguments.of("null amount",
                                                new CreateExpenseRequest("Test", null, ExpenseCategory.GROCERIES,
                                                                validDate),
                                                "amount"),
                                Arguments.of("null category",
                                                new CreateExpenseRequest("Test", new BigDecimal("25.95"), null,
                                                                validDate),
                                                "category"),
                                Arguments.of("null date",
                                                new CreateExpenseRequest("Test", new BigDecimal("25.95"),
                                                                ExpenseCategory.GROCERIES, null),
                                                "date"),
                                Arguments.of("future date",
                                                new CreateExpenseRequest("Test", new BigDecimal("25.95"),
                                                                ExpenseCategory.GROCERIES,
                                                                futureDate),
                                                "date"),
                                Arguments.of("description exceeds 255 characters",
                                                new CreateExpenseRequest(longDescription, new BigDecimal("25.95"),
                                                                ExpenseCategory.GROCERIES,
                                                                validDate),
                                                "description"),
                                Arguments.of("empty description",
                                                new CreateExpenseRequest("", new BigDecimal("25.95"),
                                                                ExpenseCategory.GROCERIES, validDate),
                                                "description"),
                                Arguments.of("amount exceeds fraction digits",
                                                new CreateExpenseRequest("Test", new BigDecimal("25.955"),
                                                                ExpenseCategory.GROCERIES,
                                                                validDate),
                                                "amount"));
        }

        @Test
        void createExpense_WithInvalidEnumValue_ShouldReturn400() throws Exception {

                Map<String, Object> invalidPayloadMap = Map.of(
                                "description", "Test",
                                "amount", 25.95,
                                "category", "INVALID_CATEGORY",
                                "date", LocalDate.now().minusDays(1).toString());

                String jsonPayload = objectMapper.writeValueAsString(invalidPayloadMap);

                mockMvc.perform(post("/api/v1/expenses")
                                .header("Authorization", "Bearer " + validToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonPayload))

                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.errors.category").value(containsString("INVALID_CATEGORY")))
                                .andExpect(jsonPath("$.errors.category")
                                                .value(containsString(ExpenseCategory.GROCERIES.name())));
        }

}
