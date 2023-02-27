package com.sweng22g1.serverapp.service;

import java.util.List;

import com.sweng22g1.serverapp.model.Map;

/**
 * 
 * This is the 'Service' layer or 'DAO' layer. Here we can implement the CRUD
 * operations / business logic for the respective database entity.
 *
 */
public interface MapService {

	/**
	 * This method will be able to update existing roles and create new ones if it
	 * doesn't already exist.
	 * 
	 * @param map The Map entity to save to the db
	 * @return The newly created/updated Map entity
	 */
	Map saveMap(Map map);

	/**
	 * Deletes a Map based on a name.
	 * 
	 * @param mapName The name the Map entity to delete
	 * @return null on successful deletion
	 */
	Map deleteMap(String mapName);

	/**
	 * Get a Map entity with a given name.
	 * 
	 * @param mapName The name of the Map to retrieve
	 * @return The retrieved Map entity
	 */
	Map getMap(String mapName);

	/**
	 * Get a list of all Maps.
	 * 
	 * @return A list of all Maps
	 */
	List<Map> getMaps();

}
