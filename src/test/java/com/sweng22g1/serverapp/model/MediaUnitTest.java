package com.sweng22g1.serverapp.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MediaUnitTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	public void createOneMediarWithFullArgsConstructor() throws Exception {
		Media newMedia = new Media(1L, "testFilepath");

		Map<String, String> expectedOut = new HashMap<String, String>();
		expectedOut.put("id", String.valueOf(1L));
		expectedOut.put("filepath", "testFilepath");

		assertEquals(expectedOut.toString(), newMedia.toString());
	}

	@Test
	public void createOneMediarWithNoArgsConstructor() throws Exception {
		Media newMedia = new Media();

		Map<String, String> expectedOut = new HashMap<String, String>();
		expectedOut.put("id", "null");
		expectedOut.put("filepath", "null");

		assertEquals(expectedOut.toString(), newMedia.toString());
	}

}
