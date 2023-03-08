package com.sweng22g1.serverapp.service;

import java.util.List;

import com.sweng22g1.serverapp.model.Post;

/**
 * 
 * This is the 'Service' layer or the 'DAO' (Data Access Object) layer. Here we
 * implement the CRUD operations / business logic for the respective database
 * entity.
 *
 */
public interface PostService {

	/**
	 * This method creates a new Post if one doesn't exist, or edits an existing
	 * post.
	 * 
	 * @param post The Post entity to save to the db
	 * @return The newly created/updated Role entity
	 */
	Post savePost(Post post);

	/**
	 * Deletes a Post entity based on a given ID.
	 * 
	 * @param id of the Post to delete
	 * @return null on successful deletion
	 */
	Post deletePost(Long id);

	/**
	 * Get a Post entity with a given ID.
	 * 
	 * @param id The ID of the Post to retrieve
	 * @return The retrieved Post entity
	 */
	Post getPost(Long id);

	/**
	 * Get a list of all Roles.
	 * 
	 * @return A list of all Roles
	 */
	List<Post> getPosts();

}
