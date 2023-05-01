package com.sweng22g1.serverapp.filter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sidharth Shanmugam
 * 
 *         A filter that extends the Spring Security
 *         UsernamePasswordAuthenticationFilter. This is to implement logic for
 *         the JWT authentication.
 *
 */
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;

	public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	/**
	 * Handle authentication attempts, this defaults to the /api/v1/login URL which
	 * is good so no need to explicitly define it
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		// Get the parameters
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		// Log the request
		log.info("Authentication attempt for username: " + username);

		// Generate an auth token
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
				password);

		// Return the authentication
		return authenticationManager.authenticate(authenticationToken);
	}

	/**
	 * This method handles a successful authentication attempt by generating valid
	 * access and refresh tokens.
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {
		// Get the user via the authentication principal
		User user = (User) authentication.getPrincipal();
		// Initialise the authentication algorithm to sign the tokens
		Algorithm algorithm = Algorithm
				.HMAC256(getEnvironment().getProperty("serverapp.jwt_token_algorithm_secret").getBytes());
		// Generate the access token
		String access_token = JWT.create().withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis()
						+ Integer.parseInt(getEnvironment().getProperty("serverapp.access_token_lifespan_minutes")) * 60
								* 1000))
				.withIssuer(request.getRequestURL().toString())
				.withClaim("roles",
						user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.sign(algorithm);
		// Generate the refresh token
		String refresh_token = JWT.create().withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis()
						+ Integer.parseInt(getEnvironment().getProperty("serverapp.refresh_token_lifespan_minutes"))
								* 60 * 1000))
				.withIssuer(request.getRequestURL().toString()).sign(algorithm);
		// Output the tokens in the response
		Map<String, String> tokens = new HashMap<>();
		tokens.put("access_token", access_token);
		tokens.put("refresh_token", refresh_token);
		// Log this
		log.info("Authentication successful for username: " + user.getUsername());
		// Set the response content type and return the response
		response.setContentType("application/json");
		new ObjectMapper().writeValue(response.getOutputStream(), tokens);
	}

}
