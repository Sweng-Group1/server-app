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

/** TEST STRATEGY
 * Integration tests between the JPA Role entity and the database. 
 * A H2 in memory database will be used in place of the mySQL production database.
 * As most repository code is provided by Spring Boot and can be considered reliable,
 * we only need to test methods defined by us in RoleRepository.
 */


@ActiveProfiles("test") // Specifies configuration file for H2 database.  
@DataJpaTest // Setups the H2 database for tests. 
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
	
	// We expect a ConstraintViolation exception when attempting to save fields
	// with a 'no null value allowed' constraint applied,
	// as per Hibernate documentation. 


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
	
	// We expect a ConstraintViolation exception when attempting to save fields
	// with a length not complying with a size constraint applied, as per Hibernate documentation. 
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
