package com.sweng22g1.serverapp.filter;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sidharth Shanmugam
 * 
 *         This filter extends Spring Framework's OncePerRequestFilter to add
 *         authorisation logic to each HTTP request that a client makes.
 *
 */
@Slf4j
public class CustomAuthorisationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (request.getServletPath().equals("/api/v1/login")
				|| request.getServletPath().equals("/api/v1/token/refresh")) {
			// No need to to perform any sort of authorisation for login and refresh token
			// requests.
			filterChain.doFilter(request, response);
		} else {
			// For any other request, authorisation is required.
			String authorisationHeader = request.getHeader(AUTHORIZATION);
			if (authorisationHeader != null && authorisationHeader.startsWith("Bearer ")) {
				try {
					// If a bearer token is provided in the request, we need to decode it, retrieve
					// the username and roles and set the authentication with the parameters
					// provided.
					String token = authorisationHeader.substring("Bearer ".length());
					Algorithm algorithm = Algorithm
							.HMAC256(getEnvironment().getProperty("serverapp.jwt_token_algorithm_secret").getBytes());
					JWTVerifier verifier = JWT.require(algorithm).build();
					DecodedJWT decodedJWT = verifier.verify(token);
					String username = decodedJWT.getSubject();
					String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
					Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
					stream(roles).forEach(role -> {
						authorities.add(new SimpleGrantedAuthority(role));
					});
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
							username, null, authorities);
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					filterChain.doFilter(request, response);

				} catch (Exception exception) {
					// If an error was caught, it probably means that the provided bearer token was
					// invalid or maybe something went wrong with the program such that the user or
					// roles are invalid. Either way this needs to be logged and a 403 error needs
					// to be returned to block the request.
					log.error("Error in CustomAuthorisationFilter: {}", exception.getMessage());
					response.setHeader("error", exception.getMessage());
					response.setStatus(FORBIDDEN.value());
					Map<String, String> error = new HashMap<>();
					error.put("error_message", exception.getMessage());
					response.setContentType("application/json");
					new ObjectMapper().writeValue(response.getOutputStream(), error);
				}
			} else {
				filterChain.doFilter(request, response);
			}
		}

	}

}
