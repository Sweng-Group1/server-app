package com.sweng22g1.serverapp.api;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
				userList = userService.getUsers();
				return ResponseEntity.ok().body(userList);
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

	@GetMapping("user/{username}")
	public ResponseEntity<User> getUser(@PathVariable("username") String usernameToRetrieve, HttpServletRequest request,
			HttpServletResponse response) {
		log.info("Attempting to return the user: " + usernameToRetrieve);
		User userToRetrieve = userService.getUser(usernameToRetrieve);
		if (userToRetrieve != null) {
			return ResponseEntity.ok().body(userToRetrieve);
		} else {
			return ResponseEntity.status(NOT_FOUND).body(userToRetrieve);
		}

	}

	/**
	 * 
	 * Endpoint to create a new user. Anyone can create a new user, however, a new
	 * user will be assigned only the 'User' role. To turn a 'User' into 'Verified'
	 * or 'Admin', a user with only either the 'Verified' or 'Admin' role has rights
	 * to perform this.
	 * 
	 * @param username  Username of the new user
	 * @param firstname Firstname of the new user
	 * @param lastname  Lastname of the new user
	 * @param email     Email of the new user
	 * @param password  Password of the new user
	 * @return Newly created user
	 */
	@PostMapping("user")
	public ResponseEntity<User> createUser(@RequestParam String username, @RequestParam String firstname,
			@RequestParam String lastname, @RequestParam String email, @RequestParam String password) {
		// Instantiate new User entity based on the input params
		User newUser = User.builder().username(username).firstname(firstname).lastname(lastname).email(email)
				.password(password).build();
		// Save the new User entity to the database
		userService.saveUser(newUser);
		// Add the "User" role to the newly created User entity
		userService.addRoleToUser(username, "User");
		// Respond with the new User
		// Spring returns a 403 when it receives an erroneous input such as creating a
		// user with an existing username.
		return ResponseEntity.ok().body(userService.saveUser(newUser));

	}

	/**
	 * 
	 * Endpoint to update an existing user. Users with the "User" role can only
	 * update their own account/User entity. Users with the "Verified" or "Admin"
	 * role can update everyone's user entity.
	 * 
	 * @param newUsername
	 * @param newFirstname
	 * @param newLastname
	 * @param newEmail
	 * @param newPassword
	 * @return The updated User entity
	 */
	@PostMapping("user/{username}")
	public ResponseEntity<User> updateUser(@RequestParam(required = false) String newUsername,
			@RequestParam(required = false) String newFirstname, @RequestParam(required = false) String newLastname,
			@RequestParam(required = false) String newEmail, @RequestParam(required = false) String newPassword,
			Principal principal, @PathVariable("username") String usernameToUpdate) {
		String usernameThatRequested = "";
		try {
			// Get the username of the user trying to make this request using the auth token
			usernameThatRequested = principal.getName();
			// Retrieve the user entity from the username
			User requestingUser = userService.getUser(usernameThatRequested);
			// Retrieve the roles of the user
			Set<Role> requestingUserRoles = requestingUser.getRoles();
			// Check if they have the "Admin" or "Verified" role or whether their username
			// matches the username of the user they're trying to update.
			if (requestingUserRoles.stream().anyMatch(p -> p.getName().equals("Admin"))
					|| requestingUserRoles.stream().anyMatch(p -> p.getName().equals("Verified"))
					|| usernameThatRequested == usernameToUpdate) {
				// Get the user entity to update
				User userToUpdate = userService.getUser(usernameToUpdate);
				if (userToUpdate != null) {
					log.info("A successful request was made by user \"" + usernameThatRequested + "\" to update user:"
							+ usernameToUpdate);
					// If the user exists, then we can safely update it's fields.
					if (newUsername != null && userToUpdate.getUsername() != newUsername) {
						// If the newUsername is given and it's different to the old username, update
						// it.
						log.info("Updating username");
						userToUpdate.setUsername(newUsername);
					}
					if (newFirstname != null && userToUpdate.getFirstname() != newFirstname) {
						// If the newFirstname is given and it's different to the old firstname, update
						// it.
						log.info("Updating firstname");
						userToUpdate.setFirstname(newFirstname);
					}
					if (newLastname != null && userToUpdate.getLastname() != newLastname) {
						// If the newLastname is given and it's different to the old lastname, update
						// it.
						log.info("Updating lastname");
						userToUpdate.setLastname(newLastname);
					}
					if (newEmail != null && userToUpdate.getEmail() != newEmail) {
						// If the newEmail is given and it's different to the old email, update it.
						log.info("Updating email address");
						userToUpdate.setEmail(newEmail);
					}
					if (newPassword != null) {
						// We don't know what the user's password is as it's encrypted. However, if the
						// user provides a newPassword in the request body, we can assume they want to
						// update that.
						log.info("Updating password");
						userToUpdate.setPassword(newPassword);
					}
					// After the fields have been updated, we can save and return the entity of the
					// updated user.
					userService.saveUser(userToUpdate);
					return ResponseEntity.ok().body(userToUpdate);
				} else {
					// Throw a 404 error if a user couldn't be found with the given
					// usernameToUpdate.
					log.error("Get all Users endpoint fail, user to update not found!");
					return ResponseEntity.status(NOT_FOUND).body(null);
				}
			} else {
				// If they are not an "Admin", "Verified" or the same user as the entity they're
				// attempting to update, the request must be blocked with a 403 and logged.
				log.warn("Request was made by user \"" + usernameThatRequested
						+ "\" to retrieve all users was blocked.");
				return ResponseEntity.status(FORBIDDEN).body(null);
			}
		} catch (Exception e) {
			// For any other errors, return a 500 code and print the exception.
			log.error("Get all Users endpoint fail, exception=" + e.getMessage());
			return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@DeleteMapping("user/{username}")
	public void deleteUser(HttpServletRequest request, HttpServletResponse response, @PathVariable String username,
			Principal principal) {
		String usernameThatRequested = "";
		try {
			// Get the username of the user trying to make this request using the auth token
			usernameThatRequested = principal.getName();
			// Retrieve the user entity from the username
			User requestingUser = userService.getUser(usernameThatRequested);
			// Retrieve the roles of the user
			Set<Role> requestingUserRoles = requestingUser.getRoles();
			// Check if they have the "Admin" or "Verified" role or whether their username
			// matches the username of the user they're trying to delete.
			if (requestingUserRoles.stream().anyMatch(p -> p.getName().equals("Admin"))
					|| requestingUserRoles.stream().anyMatch(p -> p.getName().equals("Verified"))
					|| usernameThatRequested == username) {
				// Get the user entity to delete
				User userToDelete = userService.getUser(username);
				if (userToDelete != null) {
					log.info("A successful request was made by user \"" + usernameThatRequested + "\" to delete user:"
							+ username);
					// If the user exists, then we can safely delete it.
					userService.deleteUser(username);
					response.setStatus(OK.value());
				} else {
					// Throw a 404 error if a user couldn't be found with the given
					// username.
					log.error("Delete User endpoint fail, user to delete not found!");
					response.setStatus(NOT_FOUND.value());
				}
			} else {
				// If they are not an "Admin", "Verified" or the same user as the entity they're
				// attempting to delete, the request must be blocked with a 403 and logged.
				log.warn("Request was made by user \"" + usernameThatRequested + "\" to delete user: " + username
						+ " was blocked.");
				response.setStatus(FORBIDDEN.value());
			}
		} catch (Exception e) {
			// For any other errors, return a 500 code and print the exception.
			log.error("Delete User endpoint fail, exception=" + e.getMessage());
			response.setStatus(INTERNAL_SERVER_ERROR.value());
		}
	}

}
