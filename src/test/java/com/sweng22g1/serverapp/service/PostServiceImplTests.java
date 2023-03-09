package com.sweng22g1.serverapp.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import org.assertj.core.error.OptionalShouldContainInstanceOf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.sweng22g1.serverapp.model.Post;
import com.sweng22g1.serverapp.repo.PostRepository;


/** TEST STRATEGY
 * Unit tests for the Post service layer. 
 * As the service layer entirely consists of tested methods, we only need
 * to verify the correct methods are called. Mockito is used to 
 * mock the database interactions.
 */

@ExtendWith(MockitoExtension.class)
@Rollback(true)
public class PostServiceImplTests {
	
	@Mock
	private PostRepository testPostRepository; 
	private PostService underTest;
	private AutoCloseable autoCloseable;
	
		
	
	@BeforeEach
	public void initMocks() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		underTest = new PostServiceImpl(testPostRepository);
		
	}
	
	@AfterEach
	void tearDown() throws Exception {
		autoCloseable.close();
	}
	
	@Test
	void canSavePost() {
		// Given
		LocalDateTime timestampCreated = LocalDateTime.of(2020, Month.JANUARY, 1,1, 1);
		LocalDateTime timestampUpdated = LocalDateTime.of(2020,Month.FEBRUARY, 1, 1, 1);
		LocalDateTime timestampExpiry = LocalDateTime.of(2024,Month.JANUARY, 1, 1, 1);
		double latitude = 50;
		double longitude = 45;
		Long id = 1L;

		Post testPost = Post.builder()
				.id(id)
				.latitude(latitude)
				.longitude(longitude)
				.created(timestampCreated)
				.updated(timestampUpdated)
				.expiry(timestampExpiry)
				.build();
		// When
		underTest.savePost(testPost);
		// Then
		verify(testPostRepository).save(testPost);
	}
	
	@Test
	void canDelete() {
		// Given
		LocalDateTime timestampCreated = LocalDateTime.of(2020, Month.JANUARY, 1,1, 1);
		LocalDateTime timestampUpdated = LocalDateTime.of(2020,Month.FEBRUARY, 1, 1, 1);
		LocalDateTime timestampExpiry = LocalDateTime.of(2024,Month.JANUARY, 1, 1, 1);
		double latitude = 50;
		double longitude = 45;
		Long id = 1L;

		Post testPost = Post.builder()
				.id(id)
				.latitude(latitude)
				.longitude(longitude)
				.created(timestampCreated)
				.updated(timestampUpdated)
				.expiry(timestampExpiry)
				.build();
		
		underTest.savePost(testPost);
		Optional<Post> optionalPost = Optional.of(testPost);
		// When
		when(testPostRepository.findById(id)).thenReturn(optionalPost);
		underTest.deletePost(id);
		// Then
		verify(testPostRepository).delete(testPost);
	}
	
	@Test
	void canGetPost() {
		// Given
		LocalDateTime timestampCreated = LocalDateTime.of(2020, Month.JANUARY, 1,1, 1);
		LocalDateTime timestampUpdated = LocalDateTime.of(2020,Month.FEBRUARY, 1, 1, 1);
		LocalDateTime timestampExpiry = LocalDateTime.of(2024,Month.JANUARY, 1, 1, 1);
		double latitude = 50;
		double longitude = 45;
		Long id = 1L;

		Post testPost = Post.builder()
				.id(id)
				.latitude(latitude)
				.longitude(longitude)
				.created(timestampCreated)
				.updated(timestampUpdated)
				.expiry(timestampExpiry)
				.build();
		
		underTest.savePost(testPost);
		Optional<Post> optionalPost = Optional.of(testPost);
		when(testPostRepository.findById(id)).thenReturn(optionalPost);
		// When
		Post foundPost = underTest.getPost(id);
		// Then
		verify(testPostRepository).findById(id);
	}
	
	@Test
	void canGetAllPosts() {
		// Given
		LocalDateTime timestampCreated = LocalDateTime.of(2020, Month.JANUARY, 1,1, 1);
		LocalDateTime timestampUpdated = LocalDateTime.of(2020,Month.FEBRUARY, 1, 1, 1);
		LocalDateTime timestampExpiry = LocalDateTime.of(2024,Month.JANUARY, 1, 1, 1);
		double latitude = 50;
		double longitude = 45;
		Long id = 1L;

		Post testPost = Post.builder()
				.id(id)
				.latitude(latitude)
				.longitude(longitude)
				.created(timestampCreated)
				.updated(timestampUpdated)
				.expiry(timestampExpiry)
				.build();
		
		underTest.savePost(testPost);
		// When
		List<Post> Posts = underTest.getPosts();
		// Then
		verify(testPostRepository).findAll();
	}
}