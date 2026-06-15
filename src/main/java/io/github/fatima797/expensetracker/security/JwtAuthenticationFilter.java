package io.github.fatima797.expensetracker.security;

import java.io.IOException;

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
import io.github.fatima797.expensetracker.exception.JwtErrorMessages;
import io.github.fatima797.expensetracker.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.debug("No Bearer token found for request: {} ", request.getRequestURI());
			filterChain.doFilter(request, response);
			return;
		}

		log.debug("Bearer token present on request: {}", request.getRequestURI());

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

					log.info("JWT validated successfully for user: {}", userEmail);
				} else {
					log.warn("Token validation failed for user: {}", userEmail);
				}
			}

		} catch (ExpiredJwtException e) {
			log.warn("JWT expired {}: {}", request.getRequestURI(), e.getMessage());
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, JwtErrorMessages.TOKEN_EXPIRED_ERROR,
					JwtErrorMessages.TOKEN_EXPIRED_MESSAGE);
			return;
		} catch (JwtException e) {
			log.warn("Invalid JWT: {}", request.getRequestURI(), e.getMessage());
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, JwtErrorMessages.UNAUTHORIZED_ERROR,
					JwtErrorMessages.INVALID_TOKEN_MESSAGE);
			return;
		} catch (UsernameNotFoundException e) {
			log.warn("User not found for URI {} : {}", request.getRequestURI(), e.getMessage());
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, JwtErrorMessages.UNAUTHORIZED_ERROR,
					JwtErrorMessages.JWT_USER_NOT_FOUND_MESSAGE);
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
