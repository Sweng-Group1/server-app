package com.sweng22g1.serverapp.service;

import java.util.List;

import com.sweng22g1.serverapp.model.Media;

/**
 * 
 * This is the 'Service' layer or the 'DAO' (Data Access Object) layer. Here we
 * implement the CRUD operations / business logic for the respective database
 * entity.
 *
 */
public interface MediaService {

	/**
	 * This method creates a new Media entity if one doesn't exist, or edits an
	 * existing post.
	 * 
	 * @param media The Media entity to save the db
	 * @return The newly created/updated Media entity
	 */
	Media saveMedia(Media media);

	/**
	 * Deletes a Media entity based on a given ID
	 * 
	 * @param id The ID of the Media to retrieve
	 * @return The retrieved Media entity
	 */
	Media deleteMedia(Long id);

	/**
	 * Get a Media entity with a given ID
	 * 
	 * @param id The ID of the Media entity to retrieve
	 * @return The retrieved Media entity
	 */
	Media getMedia(Long id);

	/**
	 * Get a list of all Media
	 * 
	 * @return A list of all Media
	 */
	List<Media> getMedia();

}
