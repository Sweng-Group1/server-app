package com.sweng22g1.serverapp.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoleUnitTest {

	@Test
	void createRoleWithFullArgsConstructor() {
		Role newRole = new Role(1L, "testName");
		Map<String, String> expectedOut = new HashMap<String, String>();
		expectedOut.put("id", String.valueOf(1L));
		expectedOut.put("name", "testName");
		
		assertThat(expectedOut.toString()).isEqualTo(newRole.toString());
	}
	
	@Test
	void createRoleWithNoArgsConstructor() {
		Role newRole = new Role();
		Map<String, String> expectedOut = new HashMap<String, String>();
		expectedOut.put("id", "null");
		expectedOut.put("name", "null");
		
		assertThat(expectedOut.toString()).isEqualTo(newRole.toString());
	}

}
