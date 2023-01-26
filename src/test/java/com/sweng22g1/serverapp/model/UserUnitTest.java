package com.sweng22g1.serverapp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the User class. This only tests Java class functionalities,
 * for Spring Boot specific tests, there will be another test file which tests
 * the JPA entity.
 */
public class UserUnitTest {

	// Global variable to store the current
	Date timestampNow = new Date();

	@Test
	@Order(1)
	public void createOneUserWithFullArgsConstructor() throws Exception {
		User newUser = new User(1L, "testUsername", "testFName", "testLName", "test@example.com", "testPass",
				timestampNow);

		Map<String, String> expectedOut = new HashMap<String, String>();
		expectedOut.put("id", String.valueOf(1L));
		expectedOut.put("username", "testUsername");
		expectedOut.put("firstname", "testFName");
		expectedOut.put("lastname", "testLName");
		expectedOut.put("email", "test@example.com");
		expectedOut.put("password", "testPass");
//		TODO unit test User's Role and Post associations to User.toString()  
//		params.put("roles", this.getRoles().toString());
//		params.put("posts", this.getPosts().toString());
		expectedOut.put("created", String.valueOf(timestampNow));

		System.out.println(newUser);
		System.out.println(expectedOut);

		assertEquals(expectedOut.toString(), newUser.toString());
	}

	
	@Test
	@Order(2)
	public void createOneUserWithNoArgsConstructor() throws Exception {
		User newUser = new User();

		Map<String, String> expectedOut = new HashMap<String, String>();
		expectedOut.put("id", "null");
		expectedOut.put("username", "null");
		expectedOut.put("firstname", "null");
		expectedOut.put("lastname", "null");
		expectedOut.put("email", "null");
		expectedOut.put("password", "null");
//		TODO unit test User's Role and Post associations to User.toString()  
//		params.put("roles", "null");
//		params.put("posts", th"null");
		expectedOut.put("created", "null");

		System.out.println(newUser);
		System.out.println(expectedOut);

		assertEquals(expectedOut.toString(), newUser.toString());
	}

}
