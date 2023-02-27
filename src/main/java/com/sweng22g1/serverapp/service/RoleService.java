package com.sweng22g1.serverapp.service;

import java.util.List;

import com.sweng22g1.serverapp.model.Role;

/**
 * 
 * This is the 'Service' layer or the 'DAO' (Data Access Object) layer. Here we
 * implement the CRUD operations / business logic for the respective database
 * entity.
 *
 */
public interface RoleService {

	/**
	 * This method will be able to update existing roles and create new ones if it
	 * doesn't already exist.
	 * 
	 * @param role The Role entity to save to the db
	 * @return The newly created/updated Role entity
	 */
	Role saveRole(Role role);

	/**
	 * Deletes a Role based on a name.
	 * 
	 * @param roleName The name of the Role entity to delete
	 * @return null on successful deletion
	 */
	Role deleteRole(String roleName);

	/**
	 * Get a Role entity with a given name.
	 * 
	 * @param roleName The name of the Role to retrieve
	 * @return The retrieved Role entity
	 */
	Role getRole(String roleName);

	/**
	 * Get a list of all Roles.
	 * 
	 * @return A list of all Roles
	 */
	List<Role> getRoles();

}
