package io.github.fatima797.expensetracker.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.fatima797.expensetracker.dto.SecurityErrorResponse;
import io.github.fatima797.expensetracker.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
		super();
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			LOGGER.debug("No Bearer token found for request: {} ", request.getRequestURI());
			filterChain.doFilter(request, response);
			return;
		}

		LOGGER.debug("Bearer token present on request: {}", request.getRequestURI());

		try {

			final String jwt = authHeader.substring(7);
			final String userEmail = jwtService.extractUsername(jwt);

			if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
				boolean validateToken = this.jwtService.isTokenValid(jwt, userDetails);

				if (validateToken) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
							null, userDetails.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(authToken);

					LOGGER.info("JWT validated successfully for user: {}", userEmail);
				} else {
					LOGGER.warn("Token validation failed for user: {}", userEmail);
				}
			}

		} catch (ExpiredJwtException e) {
			LOGGER.warn("JWT expired {}: {}", request.getRequestURI(), e.getMessage());
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token Expired",
					"Your session has expired. Please log in again.");
			return;
		} catch (JwtException e) {
			LOGGER.warn("Invalid JWT: {}", request.getRequestURI(), e.getMessage());
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized",
					"Invalid token. Please log in again.");
			return;
		} catch (UsernameNotFoundException e) {
			LOGGER.warn("User not found for URI {} : {}", request.getRequestURI(), e.getMessage());
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized", "User not found");
			return;
		}

		filterChain.doFilter(request, response);

	}

	private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String error, String message)
			throws IOException {

		SecurityErrorResponse errorResponse = new SecurityErrorResponse(status.value(), error, message);
		response.setStatus(status.value());
		response.setContentType("application/json");
		objectMapper.writeValue(response.getWriter(), errorResponse);
	}

}
