package com.sweng22g1.serverapp.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sweng22g1.serverapp.model.Post;
import com.sweng22g1.serverapp.model.Role;
import com.sweng22g1.serverapp.model.User;
import com.sweng22g1.serverapp.repo.PostRepository;
import com.sweng22g1.serverapp.repo.RoleRepository;
import com.sweng22g1.serverapp.repo.UserRepository;

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
public class UserServiceImpl implements UserService, UserDetailsService {

	private final UserRepository userRepo;
	private final RoleRepository roleRepo;
	private final PostRepository postRepo;
	private final PasswordEncoder passwordEncoder;

	/**
	 * Loads the Spring Security UserDetails based on the given username.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User thisUser = userRepo.findByUsername(username);
		if (thisUser == null) {
			throw new UsernameNotFoundException("User not found in the database");
		} else {
			log.info("User found in the database: \"{}\"", username);
		}
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		thisUser.getRoles().forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		});
		return new org.springframework.security.core.userdetails.User(thisUser.getUsername(), thisUser.getPassword(),
				authorities);
	}

	@Override
	public User saveUser(User user) {
		log.info("Saving User \"{}\" to the db...", user.getUsername());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepo.save(user);
	}

	@Override
	public User deleteUser(String username) {
		log.info("Deleting User \"{}\" to the db...", username);
		User thisUser = userRepo.findByUsername(username);
		userRepo.delete(thisUser);
		return null;
	}

	@Override
	public void addRoleToUser(String username, String roleName) {
		User thisUser = userRepo.findByUsername(username);
		Role thisRole = roleRepo.findByName(roleName);
		thisUser.getRoles().add(thisRole);
		log.info("Added Role \"{}\" to User \"{}\"", roleName, username);
	}

	@Override
	public void addPostToUser(String username, Long postID) {
		User thisUser = userRepo.findByUsername(username);
		Optional<Post> thisPostOptional = postRepo.findById(postID);
		if (thisPostOptional.isPresent()) {
			Post thisPost = thisPostOptional.get();
			thisUser.getPosts().add(thisPost);
			log.info("Added Post ID \"{}\" to User \"{}\"", postID, username);
		} else {
			log.info("Couldn't add Post ID \"{}\" to User \"{}\". Post not found!", postID, username);
		}
	}

	@Override
	public User getUser(String username) {
		log.info("Retrieving User \"{}\"", username);
		return userRepo.findByUsername(username);
	}

	@Override
	public List<User> getUsers() {
		log.info("Retrieving all Users...");
		return userRepo.findAll();
	}

}
