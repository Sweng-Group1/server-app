package com.sweng22g1.serverapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sweng22g1.serverapp.model.Media;

/**
 * @author Sidharth Shanmugam
 * 
 *         Interfacing the JPA to create a repository layer to be able to
 *         interact with the database table for this entity.
 *
 */
public interface MediaRepository extends JpaRepository<Media, Long> {

	Media findByFilepath(String filepath);

}
