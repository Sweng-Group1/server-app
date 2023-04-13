package com.sweng22g1.serverapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.Rollback;

import com.sweng22g1.serverapp.model.Map;
import com.sweng22g1.serverapp.repo.MapRepository;

/** TEST STRATEGY
 * Unit tests for the Map service layer.
 * As most methods used are already unit tested elements of another layer(mapRepository),
 * we just need to ensure the correct methods are being called, 
 *  
 *  Mockito is used to mock database interactions. 
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
	public void tearDown() throws Exception {
		autoCloseable.close();
	}
	
	
	@Test
	void CanCreateAndSaveMapFile() throws IOException {
		// Given
		byte[] testMapBytes = {13, 12, 42};
		String moorsMapName = "Yorkshire Moors";
		
		// When
		underTest.createMap(moorsMapName, testMapBytes);
		
		//Then
		ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);
		// Verify the save method is called and capture the argument. 
		verify(testMapRepository, times(2)).save(mapCaptor.capture());
		Map capturedMap = mapCaptor.getValue();
		String createdFilePath = capturedMap.getFilepath();
		File f = new File(createdFilePath);
		// Verify the map filepath has been generated correctly.
		assertThat(capturedMap.getFilepath()).contains("server-app/bin/main/maps/" + capturedMap.getId());
		assertThat(capturedMap.getName()).isEqualTo(moorsMapName);
		// Verify the map file has been generated.
		assertThat(f.exists()).isEqualTo(true);
		f.delete();
	}
	
	@Test
	void canDeleteMapFromDriveAndRepo() throws IOException {
		// Given
		String moorsMapName = "Yorkshire Moors";
		Map moorsMap = Map.builder()
				.name(moorsMapName)
				.filepath("yorkshire/moors")
				.id(1L)
				.build();
		
		when(testMapRepository.findByName(moorsMapName)).thenReturn(moorsMap);
		// When
		underTest.deleteMap(moorsMapName);
		
		// Then
		ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);
		verify(testMapRepository).delete(mapCaptor.capture());
		Map capturedMap = mapCaptor.getValue();
		String capturedFilePath = capturedMap.getFilepath();
		File f = new File(capturedFilePath);
		
		assertThat(f.exists()).isEqualTo(false);
	}
	
	//TODO: Add bad request tests. Delete non existent map. Can't save map?
	
}
