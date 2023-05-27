package com.sweng22g1.serverapp.api;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweng22g1.serverapp.model.Role;
import com.sweng22g1.serverapp.model.User;
import com.sweng22g1.serverapp.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * @author Sidharth Shanmugam
 * 
 *         Some sample endpoints for server related communications.
 *
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class ServerController {

	private final UserService userService;

	@Autowired
	private Environment env;

	/**
	 * @return "pong".
	 * 
	 *         Returns a pong to a ping request, this can be used to check if the
	 *         server is online.
	 */
	@GetMapping("ping")
	public ResponseEntity<String> healthCheck() {
		// Return a message to the console.
		System.out.println("ping pong!");
		// Return a response with status 200 and a body with "pong"
		return ResponseEntity.ok().body("pong");
	}

	@GetMapping("token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String authorisationHeader = request.getHeader(AUTHORIZATION);
		if (authorisationHeader != null && authorisationHeader.startsWith("Bearer ")) {
			try {

				String refresh_token = authorisationHeader.substring("Bearer ".length());
				Algorithm algorithm = Algorithm
						.HMAC256(env.getProperty("serverapp.jwt_token_algorithm_secret").getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = verifier.verify(refresh_token);
				String username = decodedJWT.getSubject();
				User user = userService.getUser(username);
				String access_token = JWT.create().withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis()
								+ Integer.parseInt(env.getProperty("serverapp.access_token_lifespan_minutes")) * 60
										* 1000))
						.withIssuer(request.getRequestURL().toString())
						.withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
						.sign(algorithm);

//				response.setHeader("access_token", access_token);
//				response.setHeader("refresh_token", refresh_token);

				Map<String, String> tokens = new HashMap<>();
				tokens.put("access_token", access_token);
				tokens.put("refresh_token", refresh_token);

				response.setContentType("application/json");
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);

			} catch (Exception exception) {
				response.setHeader("error", exception.getMessage());
//				response.sendError(FORBIDDEN.value());
				response.setStatus(FORBIDDEN.value());
				Map<String, String> error = new HashMap<>();
				error.put("error_message", exception.getMessage());
				response.setContentType("application/json");
				new ObjectMapper().writeValue(response.getOutputStream(), error);

			}
		} else {
			throw new RuntimeException("Refresh token is missing");
		}
	}

}
