package com.sweng22g1.serverapp.model;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class HashtagUnitTest {
	
	@Test
	public void createOneHastagWithCorrectFields() {
		Hashtag hashtag	= Hashtag.builder()
				.id(1L)
				.name("#LiveLaughLove")
				.latitude(50.0)
				.longitude(50.0)
				.build();
		Map<String, String> expectedOut = new HashMap<String, String>();
		expectedOut.put("id", String.valueOf(1L));
		expectedOut.put("name", "#LiveLaughLove");
		expectedOut.put("latitude", "50.0");
		expectedOut.put("longitude", "50.0");
		assertThat(expectedOut.toString()).isEqualTo(hashtag.toString());

	}
}
