package com.sweng22g1.serverapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sweng22g1.serverapp.model.Role;
import com.sweng22g1.serverapp.model.User;
import com.sweng22g1.serverapp.service.RoleService;
import com.sweng22g1.serverapp.service.UserService;

@SpringBootApplication
public class ServerAppApplication {

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(ServerAppApplication.class, args);
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * This method is run right at the start when this app is executed. This method
	 * initialises default roles and a default 'Admin' user. This should not run during
	 * tests as the H2 database conflicts.
	 */
	@Bean
	@Profile("!test")
	CommandLineRunner run(UserService userService, RoleService roleService) {
		return args -> {
			// Initialise the default roles
			roleService.saveRole(Role.builder().name("Admin").build());
			roleService.saveRole(Role.builder().name("Verified").build());
			roleService.saveRole(Role.builder().name("User").build());
			
			// Instantiate the user
			userService.saveUser(User.builder().username(env.getProperty("serverapp.default_admin_username"))
					.firstname(env.getProperty("serverapp.default_admin_firstname"))
					.lastname(env.getProperty("serverapp.default_admin_lastname"))
					.email(env.getProperty("serverapp.default_admin_email"))
					.password(env.getProperty("serverapp.default_admin_password"))
					.build());
			
			// Assign created roles to user
			userService.addRoleToUser(env.getProperty("serverapp.default_admin_username"), "Admin");
			userService.addRoleToUser(env.getProperty("serverapp.default_admin_username"), "Verified");
			userService.addRoleToUser(env.getProperty("serverapp.default_admin_username"), "User");
			System.out.println("Default admin user initialised!");
		};
	}

}
