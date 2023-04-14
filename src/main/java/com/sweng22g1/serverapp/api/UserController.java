package com.sweng22g1.serverapp.api;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sweng22g1.serverapp.model.Role;
import com.sweng22g1.serverapp.model.User;
import com.sweng22g1.serverapp.service.UserServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class UserController {

	private final UserServiceImpl userService;

	// GETting a user: If this endpoint is requested by a user with the 'Admin'
	// role, then all user entities including their own can be returned. If this
	// endpoint is requested by a user with 'User' role, only their own User entity
	// can be returned.

	@GetMapping("user")
	public ResponseEntity<List<User>> getUsers(HttpServletRequest request, HttpServletResponse response,
			Principal principal) {
		// Initialise some variables
		String usernameThatRequested = "";
		List<User> userList = null;
		try {
			// Get the username of the user trying to make this request using the auth token
			usernameThatRequested = principal.getName();
			// Retrieve the user entity from the username
			User requestingUser = userService.getUser(usernameThatRequested);
			// Retrieve the roles of the user
			Set<Role> requestingUserRoles = requestingUser.getRoles();
			// Check if they have the "Admin" role
			if (requestingUserRoles.stream().anyMatch(p -> p.getName().equals("Admin"))) {
				// If they are an "Admin", they can see a list of all users.
				log.info("A successful request was made by admin user \"" + usernameThatRequested
						+ "\" to retrieve all users.");
				return ResponseEntity.ok().body(userService.getUsers());
			} else {
				// If they are not an "Admin" the request must be blocked with a 403 and logged.
				log.warn("Request was made by user \"" + usernameThatRequested
						+ "\" to retrieve all users was blocked.");
				return ResponseEntity.status(FORBIDDEN).body(userList);
			}
		} catch (Exception e) {
			// For any other errors, return a 500 code and print the exception.
			log.error("Get all Users endpoint fail, exception=" + e.getMessage());
			return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(userList);
		}
	}

}
