package com.sweng22g1.serverapp.repo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.validation.ConstraintViolationException;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
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
	
	private static final int DATA_UPPER_SIZE_LIMIT = 100;
	private static final int PASSWORD_MINIMUM_SIZE = 8;
	/** TEST STRATEGY
	 * Integration tests between the JPA User entity and the database. 
	 * A H2 in memory database will be used in place of the mySQL production database.
	 * As methods provided by Spring Boot can be considered reliable,
	 * we only need to test methods defined by us in UserRepository.
	 * We must also test constraints such as size or whether null is allowed, 
	 * specified in the User class. 
	 */
	
	@Autowired
	private UserRepository underTest;
	
	@Rollback(true)
	@Test
	void findsByUsernameReturnsCorrectUser() {
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
	
	
	// We expect a ConstraintViolation exception when attempting to fields
	// with a 'no null value allowed' constraint applied, as per Hibernate documentation. 
	@Test
	void nullUsernameIsNotAllowed() {
		//given 
		User user = User.builder()
					.firstname("firstName")
					.lastname("lastName")
					.password("password")
					.email("user@email.com")
					.build();
		
		//then 
		assertThatThrownBy(() -> {
			underTest.save(user);
		})
		.isInstanceOf(ConstraintViolationException.class)
		.hasMessageContaining("Username cannot be null");
	}
	
	@Test
	void nullFirstNameIsNotAllowed() {
		//given 
		User user = User.builder()
				.username("username")
				.lastname("lastName")
				.password("password")
				.email("user@email.com")
				.build();
		
		//then 
		assertThatThrownBy(() -> {
			underTest.save(user);
		})
		.isInstanceOf(ConstraintViolationException.class)
		.hasMessageContaining("Firstname cannot be null");
	}
	
	@Test
	void nullLastNameIsNotAllowed() {
		//given 
		User user = User.builder()
				.username("username")
				.firstname("firstName")
				.password("password")
				.email("user@email.com")
				.build();
		
		//then 
		assertThatThrownBy(() -> {
			underTest.save(user);
		}).isInstanceOf(ConstraintViolationException.class)
		.hasMessageContaining("Lastname cannot be null");
	}
	
	@Test
	void nullPasswordIsNotAllowed() {
		//given 
		User user = User.builder()
				.username("username")
				.firstname("firstName")
				.lastname("lastName")
				.email("user@email.com")
				.build();
		
		//then 
		assertThatThrownBy(() -> {
			underTest.save(user);
		})
		.isInstanceOf(ConstraintViolationException.class)
		.hasMessageContaining("Password cannot be null");
	}
	
	@Test
	void nullEmailIsNotAllowed() {
		//given 
		User user = User.builder()
				.username("username")
				.firstname("firstName")
				.lastname("lastName")
				.password("password")
				.build();
		
		//then 
		assertThatThrownBy(() -> {
			underTest.save(user);
		})
		.isInstanceOf(ConstraintViolationException.class)
		.hasMessageContaining("Email cannot be null");
	}
	
	@Test
	void maximumSizeUsernameViolated() {
		//given 
		String name = RandomString.make(DATA_UPPER_SIZE_LIMIT + 1);
		User user = User.builder()
				.username(name)
				.firstname("firstName")
				.lastname("lastName")
				.password("password")
				.email("user@email.com")
				.build();
		
		//then 
		assertThatThrownBy(() -> {
			underTest.save(user);
		})
		.isInstanceOf(ConstraintViolationException.class)
		.hasMessageContaining("Username length");
	}
	
	@Test
	void maximumFirstNameSizeViolated() {
		//given 
		String firstName = RandomString.make(DATA_UPPER_SIZE_LIMIT + 1);
		User user = User.builder()
				.username("username")
				.firstname(firstName)
				.lastname("lastName")
				.password("password")
				.email("user@email.com")
				.build();
		
		//then 
		assertThatThrownBy(() -> {
			underTest.save(user);
		})
		.isInstanceOf(ConstraintViolationException.class)
		.hasMessageContaining("Firstname length invalid");
	}
	
	@Test
	void maximumLastNameSizeViolated() {
		//given 
		String lastName = RandomString.make(DATA_UPPER_SIZE_LIMIT + 1);
		User user = User.builder()
				.username("username")
				.firstname("firstName")
				.lastname(lastName)
				.password("password")
				.email("user@email.com")
				.build();
		
		//then 
		assertThatThrownBy(() -> {
			underTest.save(user);
		})
		.isInstanceOf(ConstraintViolationException.class)
		.hasMessageContaining("Lastname length invalid");
	}
	
	@Test
	void maximumEmailSizeViolated() {
		//given 
		String email = RandomString.make(DATA_UPPER_SIZE_LIMIT + 1);
		User user = User.builder()
				.username("username")
				.firstname("firstName")
				.lastname("lastName")
				.password("password")
				.email(email)
				.build();
		
		//then 
		assertThatThrownBy(() -> {
			underTest.save(user);
		})
		.isInstanceOf(ConstraintViolationException.class)
		.hasMessageContaining("Email length invalid");
	}
	
	@Test
	void minimumPasswordSizeViolated() {
		//given 
		String password = RandomString.make(PASSWORD_MINIMUM_SIZE - 1);
		User user = User.builder()
				.username("username")
				.firstname("firstName")
				.lastname("lastName")
				.password(password)
				.email("user@email.com")
				.build();
		
		//then 
		assertThatThrownBy(() -> {
			underTest.save(user);
		})
		.isInstanceOf(ConstraintViolationException.class)
		.hasMessageContaining("Password length too small");
	}
	
	
	
	


}
