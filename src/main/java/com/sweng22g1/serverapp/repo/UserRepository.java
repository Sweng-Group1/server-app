package com.sweng22g1.serverapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sweng22g1.serverapp.model.User;

/**
 * @author Sidharth Shanmugam
 * 
 *         Interfacing the JPA to create a repository layer to be able to
 *         interact with the database table for this entity.
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String username);

}
