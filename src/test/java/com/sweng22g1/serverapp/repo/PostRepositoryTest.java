package com.sweng22g1.serverapp.repo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.time.Month;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.sweng22g1.serverapp.model.Hashtag;
import com.sweng22g1.serverapp.model.Post;

/**
 * TEST STRATEGY Integration tests between the JPA Post entity and the database.
 * A H2 in memory database will be used in place of the mySQL production
 * database. As most repository code is provided by Spring Boot and can be
 * considered reliable, and we have not defined any new repository methods for
 * Post, we only need to test constraints, such as valid coordinates.
 */

@ActiveProfiles("test")
@DataJpaTest
public class PostRepositoryTest {

	@Autowired
	private PostRepository underTest;

	// TODO: Discuss whether we need 'post saved' test.

	@Test
	void canSaveValidPost() {
		// given
		LocalDateTime expiry = LocalDateTime.of(2030, Month.JANUARY, 1, 1, 1);
		Hashtag hashtag = Hashtag.builder().latitude(50.0).longitude(50.0).name("#MovieSociety").build();
		Post newPost = Post.builder().xmlContent("This is an XML").expiry(expiry).hashtag(hashtag).build();
			// when
		Post savedPost = underTest.save(newPost);
		assertThat(savedPost).isNotNull();
	}
	
	@Test
	void emptyPostIsNotAllowed() {
		// given
		LocalDateTime expiry = LocalDateTime.of(2024, Month.JANUARY, 1, 1, 1);
		Post newPost = Post.builder().expiry(expiry).build();
		// then
		assertThatThrownBy(() -> {
			// when
			underTest.save(newPost);
		}).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("xmlContent cannot be null");
	}
	

	/* Redundant due to location being moved to hashtag. 
	@Test
	void TooLowLongitudeValueIsNotAllowed() {
		// given
		double latitude = 50.0;
		double longitude = -181.0;
		LocalDateTime expiry = LocalDateTime.of(2024, Month.JANUARY, 1, 1, 1);
		Post newPost = Post.builder().xmlContent("xmlContent").expiry(expiry)
				.build();
		// then
		assertThatThrownBy(() -> {
			// when
			underTest.save(newPost);
		}).isInstanceOf(ConstraintViolationException.class)
				.hasMessageContaining("Latitude value invalid - less than -180");
	}
	*/ 

	/* Redundant due to location being moved to hashtag.
	@Test
	void TooHighLongitudeValueIsNotAllowed() {
		// given
		double latitude = 50.0;
		double longitude = 181.0;
		LocalDateTime expiry = LocalDateTime.of(2024, Month.JANUARY, 1, 1, 1);
		Post newPost = Post.builder().xmlContent("xmlContent").expiry(expiry)
				.build();
		// then
		assertThatThrownBy(() -> {
			// when
			underTest.save(newPost);
		}).isInstanceOf(ConstraintViolationException.class)
				.hasMessageContaining("Latitude value invalid - greater than 180");
	}
	*/

	@Test
	void UpdatedTimestampInTheFutureNotAllowed() {
		// given
		LocalDateTime updated = LocalDateTime.of(2030, Month.JANUARY, 1, 1, 1);
		Post newPost = Post.builder().xmlContent("xmlContent").updated(updated).build();
		// then
		assertThatThrownBy(() -> {
			// when
			underTest.save(newPost);
		}).isInstanceOf(ConstraintViolationException.class)
				.hasMessageContaining("Updated timestamp must be in the past.");
	}

	@Test
	void ExpiryTimestampInThePastNotAllowed() {
		// given
		LocalDateTime expiry = LocalDateTime.of(2000, Month.JANUARY, 1, 1, 1);
		Post newPost = Post.builder().xmlContent("xmlContent").expiry(expiry).build();
		// then
		assertThatThrownBy(() -> {
			// when
			underTest.save(newPost);
		}).isInstanceOf(ConstraintViolationException.class)
				.hasMessageContaining("Expiry timestamp must be in the future.");
	}
}
