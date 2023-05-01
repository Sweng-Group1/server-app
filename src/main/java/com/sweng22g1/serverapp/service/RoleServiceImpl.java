package com.sweng22g1.serverapp.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.sweng22g1.serverapp.model.Role;
import com.sweng22g1.serverapp.repo.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sidharth Shanmugam
 * 
 *         CRUD operations defined here, implementing the base Service script.
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoleServiceImpl implements RoleService {

	private final RoleRepository roleRepo;

	@Override
	public Role saveRole(Role role) {
		log.info("Saving Role \"{}\" to the db...", role.getName());
		return roleRepo.save(role);
	}

	@Override
	public Role deleteRole(String roleName) {
		log.info("Deleting Role \"{}\" to the db...", roleName);
		Role thisRole = roleRepo.findByName(roleName);
		roleRepo.delete(thisRole);
		return null;
	}

	@Override
	public Role getRole(String roleName) {
		log.info("Retrieving Role \"{}\"", roleName);
		return roleRepo.findByName(roleName);
	}

	@Override
	public List<Role> getRoles() {
		log.info("Retrieving all Roles...");
		return roleRepo.findAll();
	}

}
