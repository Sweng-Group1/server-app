package com.sweng22g1.serverapp.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import com.sweng22g1.serverapp.model.User;

@ActiveProfiles("test")
@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
	
	/**
	 * Integration tests between the JPA User entity and the database. 
	 * A H2 in memory database will be used in place of the mySQL production database.
	 * As most repository code is provided by Spring Boot and can be considered reliable,
	 * we only need to test methods defined by us in UserRepository.
	 */
	
	@Autowired
	private UserRepository underTest;
	
	@Rollback(true)
	@Test
	void FindsByUsernameReturnsCorrectUser() throws Exception {
		// given
		User user = User.builder()
				.firstname("firstName")
				.lastname("lastName")
				.password("password")
				.username("username")
				.email("user@email.com")
				.build();
		
		underTest.save(user);
		
		// when
		User foundUser = underTest.findByUsername("username");
		// then
		assertThat(user).isEqualTo(foundUser);
		
	}

}
