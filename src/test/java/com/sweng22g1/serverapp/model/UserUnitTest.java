package com.sweng22g1.serverapp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the User class. This only tests Java class functionalities,
 * for Spring Boot specific tests, there will be another test file which tests
 * the JPA entity.
 */
public class UserUnitTest {

	// Global variable to store the current
	LocalDateTime timestampNow = LocalDateTime.now();

	@Test
	@Order(1)
	public void createOneUserWithFullArgsConstructor() throws Exception {
		Role newRole = new Role(1L, "testRole");
		LocalDateTime timestampCreated = LocalDateTime.of(2020, Month.JANUARY, 1,1, 1);
		LocalDateTime timestampUpdated = LocalDateTime.of(2020,Month.FEBRUARY, 1, 1, 1);
		LocalDateTime timestampExpiry = LocalDateTime.of(2024,Month.JANUARY, 1, 1, 1);
		double latitude = 50;
		double longitude = 45;
		
		Post newPost = new Post
				(1L, "XMLContent", timestampCreated, timestampUpdated, timestampExpiry, latitude, longitude);
		
		//TODO: Confirm whether sets 'no duplicates' restriction will be okay. 
		Set<Role> roles = new HashSet<Role>();
		roles.add(newRole);
		
		Set<Post> posts = new HashSet<Post>();
		posts.add(newPost);
		
		User newUser = new User(1L, "testUsername", "testFName", "testLName", "test@example.com", "testPass",
				timestampNow, roles, posts);

		Map<String, String> expectedOut = new HashMap<String, String>();
		expectedOut.put("id", String.valueOf(1L));
		expectedOut.put("username", "testUsername");
		expectedOut.put("firstname", "testFName");
		expectedOut.put("lastname", "testLName");
		expectedOut.put("email", "test@example.com");
		expectedOut.put("password", "testPass");
		expectedOut.put("roles", roles.toString());
		expectedOut.put("posts", posts.toString());
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
		expectedOut.put("roles", "[]");
		expectedOut.put("posts", "[]");
		expectedOut.put("created", "null");

		System.out.println(newUser);
		System.out.println(expectedOut);

		assertEquals(expectedOut.toString(), newUser.toString());
	}

}
