package com.sweng22g1.serverapp.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class HashtagUnitTest {
	
	@Test
	public void createOneHastagWithCorrectFields() {
		
		Hashtag hashtag	= Hashtag.builder().id(1L).name("#LiveLaughLove").build();
		Map<String, String> expectedOut = new HashMap<String, String>();
		expectedOut.put("id", String.valueOf(1L));
		expectedOut.put("name", "#LiveLaughLove");
		assertThat(expectedOut.toString()).isEqualTo(hashtag.toString());

	}
	

}
