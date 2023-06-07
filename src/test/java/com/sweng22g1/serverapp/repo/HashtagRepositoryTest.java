package com.sweng22g1.serverapp.repo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.validation.ConstraintViolationException;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.sweng22g1.serverapp.model.Hashtag;

@ActiveProfiles("test")
@DataJpaTest
public class HashtagRepositoryTest {

	private static final int NAME_UPPER_SIZE_LIMIT = 100;
	@Autowired
	private HashtagRepository underTest;

	@Test
	public void canFindByName() {

		String name = "#LiveLaughLove";
		Hashtag hashtag = Hashtag.builder().id(1L).name(name).build();
		underTest.save(hashtag);
		Hashtag foundHashtag = underTest.findByName(name);

		assertThat(foundHashtag).isEqualTo(hashtag);
	}

	@Test
	public void nameCannotBeNull() {
		String name = null;
		Hashtag hashtag = Hashtag.builder().id(1L).name(name).build();

		assertThatThrownBy(() -> {
			underTest.save(hashtag);
		}).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Name cannot be null");
	}

	@Test
	public void nameCannotBeLongerThanMaximumLength() {
		String name = RandomString.make(NAME_UPPER_SIZE_LIMIT + 1);
		Hashtag hashtag = Hashtag.builder().id(1L).name(name).build();

		assertThatThrownBy(() -> {
			underTest.save(hashtag);
		}).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Name length invalid");
	}

	@Test
	public void namesMustBeUnique() {
		String name = "#LiveLaughLove";
		Hashtag hashtag = Hashtag.builder().id(1L).name(name).build();
		Hashtag hashtag2 = Hashtag.builder().id(2L).name(name).build();
		underTest.save(hashtag);
		
		assertThatThrownBy(() -> {
			underTest.save(hashtag2);
		}).hasMessageContaining("ConstraintViolationException");
	}

	@Test
	public void idBeingGeneratedByRepository() {
		String name = "#LiveLaughLove";
		Hashtag hashtag = Hashtag.builder().id(null).name(name).build();
		underTest.save(hashtag);
		Hashtag foundHashtag = underTest.findByName(name);

		assertThat(foundHashtag.getId()).isNotEqualTo(null);
	}
	
	@Test
	void TooLowLatitudeValueIsNotAllowed() {
		// given
		double latitude = -91.0;
		double longitude = 50.0;
		Hashtag newHashtag = Hashtag.builder().latitude(latitude).longitude(longitude)
				.name("#LiveLaughLove")
				.build();
		// then
		assertThatThrownBy(() -> {
			// when
			underTest.save(newHashtag);
		}).isInstanceOf(ConstraintViolationException.class)
				.hasMessageContaining("Latitude value invalid - less than -90");
	}

	@Test
	void TooHighLatitudeValueIsNotAllowed() {
		// given
		double latitude = 91.0;
		double longitude = 50.0;
		Hashtag newHashtag = Hashtag.builder().latitude(latitude).longitude(longitude)
				.name("#LiveLaughLove")
				.build();
		// then
		assertThatThrownBy(() -> {
			// when
			underTest.save(newHashtag);
		}).isInstanceOf(ConstraintViolationException.class)
				.hasMessageContaining("Latitude value invalid - greater than 90");
	}
}
