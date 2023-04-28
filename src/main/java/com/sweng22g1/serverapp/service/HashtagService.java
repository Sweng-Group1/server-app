package com.sweng22g1.serverapp.service;

import java.util.List;

import com.sweng22g1.serverapp.model.Hashtag;

/**
 * @author Sidharth Shanmugam
 * 
 * This is the 'Service' layer or the 'DAO' (Data Access Object) layer. Here we
 * implement the CRUD operations / business logic for the respective database
 * entity.
 *
 */
public interface HashtagService {
	
	/**
	 * This method will be able to update existing roles and create new ones if it
	 * doesn't already exist.
	 * 
	 * @param role The Hashtag entity to save to the db
	 * @return The newly created/updated Hashtag entity
	 */
	Hashtag saveHashtag(Hashtag hashtag);

	/**
	 * Deletes a Hashtag based on a name.
	 * 
	 * @param HashtagName The name of the Hashtag entity to delete
	 * @return null on successful deletion
	 */
	Hashtag deleteHashtag(String hashtagName);

	/**
	 * Get a Hashtag entity with a given name.
	 * 
	 * @param HashtagName The name of the Hashtag to retrieve
	 * @return The retrieved Role entity
	 */
	Hashtag getHashtag(String hashtagName);

	/**
	 * Get a list of all Hashtag.
	 * 
	 * @return A list of all Hashtag
	 */
	List<Hashtag> getHashtag();

}
