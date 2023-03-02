package com.sweng22g1.serverapp.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.Rollback;

import com.sweng22g1.serverapp.model.Map;
import com.sweng22g1.serverapp.repo.MapRepository;;

/** TEST STRATEGY
 * Unit tests for the Map service layer. 
 * As the service layer entirely consists of tested methods, we only need
 * to verify the correct methods are called. Mockito is used to 
 * mock the database interactions.
 */

@ExtendWith(MockitoExtension.class)
@Rollback(true)
public class MapServiceImpTests {
	
	@Mock
	private MapRepository testMapRepository; 
	private MapService underTest;
	private AutoCloseable autoCloseable;
	
		
	
	@BeforeEach
	public void initMocks() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		underTest = new MapServiceImpl(testMapRepository);
		
	}
	
	@AfterEach
	void tearDown() throws Exception {
		autoCloseable.close();
	}
	
	@Test
	void canSaveMap() {
		// Given
		String mapName = "Yorkshire Moors";
		Map moorsMap = Map.builder()
				.name(mapName)
				.filepath("yorkshire/moors")
				.id(1L)
				.build();
		// When
		underTest.saveMap(moorsMap);
		// Then
		verify(testMapRepository).save(moorsMap);
	}
	
	@Test
	void canDeleteMap() {
		// Given
		String mapName = "Yorkshire Moors";
		Map moorsMap = Map.builder()
				.name(mapName)
				.filepath("yorkshire/moors")
				.id(1L)
				.build();
		
		underTest.saveMap(moorsMap);
		// When
		when(testMapRepository.findByName(mapName)).thenReturn(moorsMap);
		underTest.deleteMap(mapName);
		// Then
		verify(testMapRepository).delete(moorsMap);
	}
	
	@Test
	void canGetMap() {
		// Given
		String mapName = "Yorkshire Moors";
		Map moorsMap = Map.builder()
				.name(mapName)
				.filepath("yorkshire/moors")
				.id(1L)
				.build();
		
		underTest.saveMap(moorsMap);
		// When
		Map foundMap = underTest.getMap(mapName);
		// Then
		verify(testMapRepository).findByName(mapName);
	}
	
	@Test
	void canGetAllMaps() {
		// Given
		String mapName = "Yorkshire Moors";
		Map moorsMap = Map.builder()
				.name(mapName)
				.filepath("yorkshire/moors")
				.id(1L)
				.build();
		
		underTest.saveMap(moorsMap);
		// When
		List<Map> maps = underTest.getMaps();
		// Then
		verify(testMapRepository).findAll();
	}
}
