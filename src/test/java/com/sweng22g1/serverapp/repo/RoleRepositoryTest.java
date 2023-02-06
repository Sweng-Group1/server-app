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

import com.sweng22g1.serverapp.model.Role;
import com.sweng22g1.serverapp.model.User;

@ActiveProfiles("test")
@DataJpaTest
public class RoleRepositoryTest {
	
	private static final int DATA_UPPER_SIZE_LIMIT = 100;
	
	@Autowired
	private RoleRepository underTest;
	
	@Rollback(true)
	@Test
	void findsByUsernameReturnsCorrectUser() {
		// given
		Role role = Role.builder()
				.name("name")
				.build();
		
		underTest.save(role);
		
		// when
		Role foundRole = underTest.findByName("name");
		// then
		assertThat(role).isEqualTo(foundRole);
	}
	
	@Test
	void nullNameIsNotAllowed() {
		// given
		Role role = Role.builder()
					.build();
		
		// then
		assertThatThrownBy(() -> {
			underTest.save(role);
		})
		.isInstanceOf(ConstraintViolationException.class)
		.hasMessageContaining("Name cannot be null");
	}
	
	@Test
	void maximumNameSizeViolated() {
		//given 
		String name = RandomString.make(DATA_UPPER_SIZE_LIMIT + 1);
		Role role = Role.builder()
					.name(name)
					.build();
		
		//then 
		assertThatThrownBy(() -> {
			underTest.save(role);
		})
		.isInstanceOf(ConstraintViolationException.class)
		.hasMessageContaining("Name length invalid");
	}
	
}
