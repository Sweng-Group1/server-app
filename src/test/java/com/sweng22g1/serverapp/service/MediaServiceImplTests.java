package com.sweng22g1.serverapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sweng22g1.serverapp.model.Media;
import com.sweng22g1.serverapp.repo.MediaRepository;

/**
 * TEST STRATEGY Unit tests for the Media service layer. As the service layer
 * almost entirely consists of tested / standard Java methods (Files), we mostly
 * need to just verify the correct methods are called. The File methods are
 * tested for correct operation. Mockito is used to mock the database
 * interactions.
 */

@ExtendWith(MockitoExtension.class)
public class MediaServiceImplTests {

	@Mock
	private MediaRepository testMediaRepository;
	private MediaService underTest;
	private AutoCloseable autoCloseable;

	@BeforeEach
	public void initMocks() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		underTest = new MediaServiceImpl(testMediaRepository);

	}

	@AfterEach
	void tearDown() throws Exception {
		autoCloseable.close();
	}

	@Test
	void CanCreateAndSaveMediaFile() throws IOException {
		// Given
		byte[] testMediaBytes = { 12, 14 };

		// When
		underTest.createMedia(testMediaBytes);

		// Then
		ArgumentCaptor<Media> mediaCaptor = ArgumentCaptor.forClass(Media.class);
		// Verify the save method is called.
		verify(testMediaRepository).save(mediaCaptor.capture());
		Media capturedMedia = mediaCaptor.getValue();
		String createdFilePath = capturedMedia.getFilepath();
		// Verify the media filepath has been generated correctly.
		assertThat(capturedMedia.getFilepath()).contains("server-app/bin/main/media/" + capturedMedia.getId());
		File f = new File(createdFilePath);
		// Verify the media file has been generated.
		assertThat(f.exists()).isEqualTo(true);
	}

	@Test
	void CanDeleteMedia() throws IOException {
		// Given
		long testID = 5L;
		Media testMedia = Media.builder().id(testID).filepath("test/Filepath").build();

		Optional<Media> testOptionalMedia = Optional.of(testMedia);

		// When
		when(testMediaRepository.findById(testID)).thenReturn(testOptionalMedia);
		underTest.deleteMedia(testID);

		// Then
		// Verify the delete method is called when media is present.
		verify(testMediaRepository).delete(testMedia);

	}

	@Test
	void returnsNullIfAttemptedToDeleteNonExistingMedia() throws IOException {

		long testID = 5L;
		Media.builder().id(testID).filepath("test/Filepath").build();

		Optional<Media> testOptionalMedia = Optional.ofNullable(null);

		// When
		when(testMediaRepository.findById(testID)).thenReturn(testOptionalMedia);
		testOptionalMedia = Optional.ofNullable(underTest.deleteMedia(testID));
		// Then
		assertThat(testOptionalMedia.isEmpty()).isEqualTo(true);
	}

}