package com.sweng22g1.serverapp.repo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.validation.ConstraintViolationException;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import com.sweng22g1.serverapp.model.Media;

/**
 * TEST STRATEGY Integration tests between the JPA Media entity and the
 * database. A H2 in memory database will be used in place of the mySQL
 * production database. As most repository code is provided by Spring Boot and
 * can be considered reliable, we only need to test methods defined by us in
 * MediaRepository.
 * 
 * @author Paul Pickering
 */
@ActiveProfiles("test")
@DataJpaTest
class MediaRepositoryTest {

	private static final int DATA_UPPER_SIZE_LIMIT = 255;
	@Autowired
	private MediaRepository underTest;

	@Rollback(true)
	@Test
	void findsByFilepathReturnsCorrectMedia() {
		// given
		Media media = Media.builder().filepath("test/filepath").mimetype("text/plain").build();

		underTest.save(media);

		// when
		Media foundMedia = underTest.findByFilepath("test/filepath");
		// then
		assertThat(foundMedia).isEqualTo(media);
	}

	// We expect a ConstraintViolation exception when attempting to save fields
	// with a 'no null value allowed' constraint applied, as per Hibernate
	// documentation.
	void nullFilepathIsNotAllowed() {
		// given
		Media media = Media.builder().build();

		// then
		assertThatThrownBy(() -> {
			underTest.save(media);
		}).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Filepath cannot be null");
	}

	// We expect a ConstraintViolation exception when attempting to save fields
	// with a length not complying with a size constraint applied, as per Hibernate
	// documentation.
	@Test
	void maximumFilepathSizeViolated() {
		// given
		String filepath = RandomString.make(DATA_UPPER_SIZE_LIMIT + 1);
		Media media = Media.builder().filepath(filepath).build();

		// then
		assertThatThrownBy(() -> {
			underTest.save(media);
		}).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Filepath length invalid");
	}

}
