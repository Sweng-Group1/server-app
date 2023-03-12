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

import com.sweng22g1.serverapp.model.Map;

/**
 * TEST STRATEGY Integration tests between the JPA Map entity and the database.
 * A H2 in memory database will be used in place of the mySQL production
 * database. As most repository code is provided by Spring Boot and can be
 * considered reliable, we only need to test methods defined by us in
 * MapRepository.
 */

@ActiveProfiles("test")
@DataJpaTest
public class MapRepositoryTest {

	@Autowired
	private MapRepository underTest;

	@Rollback(true)
	@Test
	void findsByFilepathReturnsCorrectMap() {
		// given
		Map newMap = Map.builder().name("York").filepath("test/filepath").build();

		underTest.save(newMap);

		// when
		Map foundMap = underTest.findByFilepath("test/filepath");
		// then
		assertThat(foundMap).isEqualTo(newMap);
	}

	@Test
	void findsByNameReturnsCorrectMap() {
		// given
		Map newMap = Map.builder().name("York").filepath("test/filepath").build();

		underTest.save(newMap);

		// when
		Map foundMap = underTest.findByName("York");
		// then
		assertThat(newMap).isEqualTo(foundMap);
	}

	@Test
	void IncorrectSearchReturnsNull() {
		// given
		Map newMap = Map.builder().name("York").filepath("test/filepath").build();

		underTest.save(newMap);

		// when
		Map foundMap = underTest.findByFilepath("test/filepath2");
		// then
		assertThat(foundMap).isEqualTo(null);
	}

	@Test
	void maximumFilepathSizeViolationThrowsException() {
		// given
		String filepath = RandomString.make(255 + 1);
		Map newMap = Map.builder().filepath(filepath).name("York").build();

		// then
		assertThatThrownBy(() -> {
			underTest.save(newMap);
		}).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Map filepath length invalid");
	}

	@Test
	void maximumNameSizeViolationThrowsException() {
		// given
		String name = RandomString.make(100 + 1);
		Map newMap = Map.builder().filepath("filepath").name(name).build();

		// then
		assertThatThrownBy(() -> {
			underTest.save(newMap);
		}).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Map name length invalid");
	}

	@Test
	void nullFilepathIsNotAllowed() {
		// given
		Map newMap = Map.builder().name("York").build();

		// then
		assertThatThrownBy(() -> {
			underTest.save(newMap);
		}).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Map filepath cannot be null");
	}

	@Test
	void nullNameNotAllowed() {
		// given
		Map newMap = Map.builder().filepath("filepath").build();

		// then
		assertThatThrownBy(() -> {
			underTest.save(newMap);
		}).isInstanceOf(ConstraintViolationException.class).hasMessageContaining("Map name cannot be null");
	}
}
