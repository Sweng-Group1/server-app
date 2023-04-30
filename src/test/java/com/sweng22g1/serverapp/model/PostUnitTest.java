package com.sweng22g1.serverapp.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class PostUnitTest {

	@Test
	public void createOnePostWithFullArgsConstructor() throws Exception {

		LocalDateTime timestampCreated = LocalDateTime.of(2020, Month.JANUARY, 1, 1, 1);
		LocalDateTime timestampUpdated = LocalDateTime.of(2020, Month.FEBRUARY, 1, 1, 1);
		LocalDateTime timestampExpiry = LocalDateTime.of(2024, Month.JANUARY, 1, 1, 1);
		double latitude = 50;
		double longitude = 45;
		Hashtag hashtag	= Hashtag.builder().id(1L).name("#LiveLaughLove").build();

		Post newPost = new Post(1L, "XMLContent", timestampCreated, timestampUpdated, timestampExpiry, latitude,
				longitude, hashtag);

		Map<String, String> expectedOut = new HashMap<String, String>();
		expectedOut.put("id", String.valueOf(1L));
		expectedOut.put("created", timestampCreated.toString());
		expectedOut.put("updated", timestampUpdated.toString());
		expectedOut.put("expiry", timestampExpiry.toString());
		expectedOut.put("latitude", String.valueOf(latitude));
		expectedOut.put("longitude", String.valueOf(longitude));
		expectedOut.put("hashtag", hashtag.toString());
		expectedOut.put("xmlContent", "XMLContent");

		System.out.println(newPost);
		System.out.println(expectedOut);

		assertEquals(expectedOut.toString(), newPost.toString());
	}

}
