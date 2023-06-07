package com.sweng22g1.serverapp.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.Rollback;

import com.sweng22g1.serverapp.model.Hashtag;
import com.sweng22g1.serverapp.repo.HashtagRepository;

// Test Strategy: Just need to ensure the service layer is calling the correct 
// repository methods. 

// @author Paul Pickering

@ExtendWith(MockitoExtension.class)
@Rollback(true)
public class HashtagServiceImplTests {

	@Mock
	private HashtagRepository testHashtagRepository;

	private HashtagService underTest;
	private AutoCloseable autoCloseable;

	@BeforeEach
	public void initMocks() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		underTest = new HashtagServiceImpl(testHashtagRepository);
	}

	@AfterEach
	void tearDown() throws Exception {
		autoCloseable.close();
	}

	@Test
	public void canSaveHashtag() {
		Hashtag hashtag = Hashtag.builder().name("#LiveLaughLove").build();
		underTest.saveHashtag(hashtag);
		verify(testHashtagRepository).save(hashtag);
	}

	@Test
	public void canDeleteHashtag() {
		String name = "#LiveLaughLove";
		Hashtag hashtag = Hashtag.builder().id(1L).name(name).build();
		when(testHashtagRepository.findByName(name)).thenReturn(hashtag);
		underTest.deleteHashtag(name);
		verify(testHashtagRepository).delete(hashtag);
	}

	@Test
	public void canGetHashtag() {
		String name = "#LiveLaughLove";
		underTest.getHashtag(name);
		verify(testHashtagRepository).findByName(name);
	}

	@Test
	public void canGetAllHashtags() {
		String name = "#LiveLaughLove";
		underTest.getHashtags();
		verify(testHashtagRepository).findAll();
	}

}
