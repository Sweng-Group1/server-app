package com.sweng22g1.serverapp.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;


import org.junit.jupiter.api.Test;

public class MapUnitTest {

	
	@Test
	public void createOneMaprWithFullArgsConstructor() throws Exception {
		Map newMap = new Map(1L, "York", "testFilepath");

		assertThat(newMap.getId()).isEqualTo(1L);
		assertThat(newMap.getFilepath()).isEqualTo("testFilepath");
		assertThat(newMap.getName()).isEqualTo("York");
	}
}
