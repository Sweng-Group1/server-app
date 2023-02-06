package com.sweng22g1.serverapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sweng22g1.serverapp.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findByName(String name);
}
