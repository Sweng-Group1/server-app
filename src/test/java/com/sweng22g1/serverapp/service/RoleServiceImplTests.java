package com.sweng22g1.serverapp.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.sweng22g1.serverapp.model.Role;
import com.sweng22g1.serverapp.repo.RoleRepository;


/** TEST STRATEGY
 * Unit tests for the Role service layer. 
 * As the service layer entirely consists of tested methods, we only need
 * to verify the correct methods are called. Mockito is used to 
 * mock the database interactions.
 */

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@Rollback(true)
public class RoleServiceImplTests {
	
	@Mock
	private RoleRepository testRoleRepository; 
	private RoleService underTest;
	private AutoCloseable autoCloseable;
	
		
	
	@BeforeEach
	public void initMocks() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		underTest = new RoleServiceImpl(testRoleRepository);
		
	}
	
	@AfterEach
	void tearDown() throws Exception {
		autoCloseable.close();
	}
	
	@Test
	void canSaveRole() {
		// Given
		String RoleName = "YUSU Admin";
		Role testRole = Role.builder()
				.name(RoleName)
				.build();
		// When
		underTest.saveRole(testRole);
		// Then
		verify(testRoleRepository).save(testRole);
	}
	
	@Test
	void canDelete() {
		// Given
		String RoleName = "Yorkshire Moors";
		Role moorsRole = Role.builder()
				.name(RoleName)
				.build();
		
		underTest.saveRole(moorsRole);
		// When
		when(testRoleRepository.findByName(RoleName)).thenReturn(moorsRole);
		underTest.deleteRole(RoleName);
		// Then
		verify(testRoleRepository).delete(moorsRole);
	}
	
	@Test
	void canGetRole() {
		// Given
		String RoleName = "Yorkshire Moors";
		Role testRole = Role.builder()
				.name(RoleName)
				.build();
		
		underTest.saveRole(testRole);
		// When
		Role foundRole = underTest.getRole(RoleName);
		// Then
		verify(testRoleRepository).findByName(RoleName);
	}
	
	@Test
	void canGetAllRoles() {
		// Given
		String RoleName = "Yorkshire Moors";
		Role testRole = Role.builder()
				.name(RoleName)
				.build();
		
		underTest.saveRole(testRole);
		// When
		List<Role> Roles = underTest.getRoles();
		// Then
		verify(testRoleRepository).findAll();
	}
}