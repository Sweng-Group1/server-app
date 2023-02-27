package com.sweng22g1.serverapp.service;

import java.util.List;

import com.sweng22g1.serverapp.model.User;

/**
 * 
 * This is the 'Service' layer or the 'DAO' (Data Access Object) layer. Here we
 * implement the CRUD operations for the respective database entity.
 *
 */
public interface UserService {

	/**
	 * Two birds with one stone: creates a new user if one doesn't exist, updates if
	 * one already exists. Creates if not exists, updates if already exists. This is
	 * done based on the entity ID.
	 * 
	 * @param user The user object to save on to db
	 * @return The newly created/updated User entity
	 */
	User saveUser(User user);

	/**
	 * Deletes a user based on a username
	 * 
	 * @param username The username of the User entity to delete
	 * @return null on successful deletion
	 */
	User deleteUser(String username);

	/**
	 * Add a role to a user, both referenced by their respective names.
	 * 
	 * @param username The user to add the role to
	 * @param roleName The role which needs to be added
	 */
	void addRoleToUser(String username, String roleName);

	/**
	 * Add a post to a user, user referenced by the username and post referenced by
	 * the ID.
	 * 
	 * @param username The user to add the post to
	 * @param postID   The post which needs to be added
	 */
	void addPostToUser(String username, Long postID);

	/**
	 * Get a user entity with a given username.
	 * 
	 * @param Username THe username of the User to retrieve
	 * @return The retrieved user entity
	 */
	User getUser(String username);

	/**
	 * Get a list of all users.
	 * 
	 * @return A list of all users
	 */
	List<User> getUsers();

}
