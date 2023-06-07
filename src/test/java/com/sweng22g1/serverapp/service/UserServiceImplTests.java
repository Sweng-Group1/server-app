package com.sweng22g1.serverapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import com.sweng22g1.serverapp.model.Post;
import com.sweng22g1.serverapp.model.Role;
import com.sweng22g1.serverapp.model.User;
import com.sweng22g1.serverapp.repo.PostRepository;
import com.sweng22g1.serverapp.repo.RoleRepository;
import com.sweng22g1.serverapp.repo.UserRepository;

/**
 * @author Paul Pickering
 * TEST STRATEGY Unit tests for the User service layer. As the service layer
 * entirely consists of tested methods, we only need to verify the correct
 * methods are called. Mockito is used to mock the database interactions.
 */

@ExtendWith(MockitoExtension.class)
@Rollback(true)
public class UserServiceImplTests {

	@Mock
	private UserRepository testUserRepository;
	@Mock
	private RoleRepository testRoleRepository;
	@Mock
	private PostRepository testPostRepository;
	@Mock
	private PasswordEncoder testPasswordEncoder;
	private UserService underTest;
	private AutoCloseable autoCloseable;

	@BeforeEach
	public void initMocks() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		underTest = new UserServiceImpl(testUserRepository, testRoleRepository, testPostRepository,
				testPasswordEncoder);

	}

	@AfterEach
	void tearDown() throws Exception {
		autoCloseable.close();
	}

	@Test
	void canSaveUser() {
		// Given
		Long id = 1L;

		User testUser = User.builder().id(id).firstname("firstName").lastname("lastName").password("password")
				.username("username").email("email").id(id).build();
		// When
		underTest.saveUser(testUser);
		// Then
		verify(testUserRepository).save(testUser);
	}

	@Test
	void canDelete() {
		// Given
		Long id = 1L;
		String username = "username";
		User testUser = User.builder().id(id).firstname("firstName").lastname("lastName").password("password")
				.username(username).email("email").id(id).build();
		// When
		when(testUserRepository.findByUsername(username)).thenReturn(testUser);
		underTest.deleteUser(username);
		// Then
		verify(testUserRepository).delete(testUser);
	}

	@Test
	void canGetUser() {
		// Given
		Long id = 1L;
		String username = "username";
		User testUser = User.builder().id(id).firstname("firstName").lastname("lastName").password("password")
				.username(username).email("email").id(id).build();

		when(testUserRepository.findByUsername(username)).thenReturn(testUser);
		// When
		underTest.getUser(username);
		// Then
		verify(testUserRepository).findByUsername(username);
	}

	@Test
	void canGetAllUsers() {
		// Given
		Long id = 1L;
		String username = "username";
		User.builder().id(id).firstname("firstName").lastname("lastName").password("password").username(username)
				.email("email").id(id).build();

		// When
		underTest.getUsers();
		// Then
		verify(testUserRepository).findAll();
	}

	@Test
	void canAddRoleToUser() {
		// Given
		Long id = 1L;
		String username = "username";
		String roleName = "role";

		User testUser = User.builder().id(id).firstname("firstName").lastname("lastName").password("password")
				.username(username).email("email").id(id).build();

		Role role = Role.builder().name(roleName).id(1L).build();

		when(testUserRepository.findByUsername(username)).thenReturn(testUser);
		when(testRoleRepository.findByName(roleName)).thenReturn(role);
		// When
		underTest.addRoleToUser(username, roleName);

		// Then
		assertThat(testUser.getRoles().iterator().next().getName()).isEqualTo(roleName);
	}

	@Test
	void canAddPostToUser() {
		// Given
		Long id = 1L;
		Long postId = 2L;
		String username = "username";

		User testUser = User.builder().id(id).firstname("firstName").lastname("lastName").password("password")
				.username(username).email("email").id(id).build();

		LocalDateTime expiry = LocalDateTime.of(2024, Month.JANUARY, 1, 1, 1);
		Post newPost = Post.builder().expiry(expiry).id(postId).build();

		Optional<Post> optionalPost = Optional.of(newPost);

		when(testUserRepository.findByUsername(username)).thenReturn(testUser);
		when(testPostRepository.findById(postId)).thenReturn(optionalPost);
		// When
		underTest.addPostToUser(username, postId);

		// Then
		assertThat(testUser.getPosts().iterator().next().getId()).isEqualTo(postId);
	}

}