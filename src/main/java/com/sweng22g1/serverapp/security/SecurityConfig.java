package com.sweng22g1.serverapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sweng22g1.serverapp.filter.CustomAuthenticationFilter;
import com.sweng22g1.serverapp.filter.CustomAuthorisationFilter;

/**
 * @author Sidharth Shanmugam
 * 
 *         This extends Spring Security's WebSecurityConfigurerAdapter to define
 *         a security configuration to block or permit endpoints based on user
 *         roles.
 * 
 *         WebSecurityConfigurerAdapter is depreciated in this release of Spring
 *         Security, however, still exists and functions as intended. I have
 *         chosen not to use the SecurityFilterChain bean, which replaces this
 *         class, as the documentation for that is not great. In future
 *         releases, as the documentation grows for the replacement, the switch
 *         should be made to ensure high stability.
 *
 */
@Configuration
@EnableWebSecurity
@SuppressWarnings("deprecation")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private Environment env;

	public SecurityConfig(UserDetailsService userDetailService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userDetailService = userDetailService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// Gets the user details and encode the password
		auth.userDetailsService(userDetailService).passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(
				authenticationManagerBean());
		CustomAuthorisationFilter customAuthorisationFilter = new CustomAuthorisationFilter();
		customAuthenticationFilter.setEnvironment(env);
		customAuthorisationFilter.setEnvironment(env);
		customAuthenticationFilter.setFilterProcessesUrl("/api/v1/login");
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.authorizeRequests().antMatchers("/api/v1/ping").permitAll();
		http.authorizeRequests().antMatchers("/api/v1/login/**", "/api/v1/token/refresh/**").permitAll();

		// GET Post - anyone can get posts, in the endpoint (controller layer) we need
		// to define rules to make sure logged out users can only see verified user
		// posts.
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/v1/post/**").permitAll();
		// All users should be able to access the DELETE endpoint, however, controller
		// needs to check if the requesting user can delete that post
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/v1/post/**").permitAll();
		// POST Post - all logged in users can upload a post
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/v1/post/**").hasAnyAuthority("User", "Verified",
				"Admin");

		// GET Hashtags - anyone can get hashtags
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/v1/hashtag/**").permitAll();
		// POST Hashtags - only admin and verified users can update hashtags
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/v1/hashtag/**").hasAnyAuthority("Verified",
				"Admin");

		// GET Media - anyone can get media
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/v1/media/**").permitAll();
		// POST Media - all logged in users can upload media
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/v1/media/**").hasAnyAuthority("User", "Verified",
				"Admin");
		// DELETE Media - Only admins or verified users can DELETE media entities
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/v1/media/**").hasAnyAuthority("Verified",
				"Admin");

		// POST User - no authentication is needed to create users however if a user is
		// not verified or an admin they can only modify their own entity.
		// TODO service layer this rule.
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/v1/user/**").permitAll();
		// GET User - logged in users can get users.
		// TODO: Add controller layer logic to make sure any user that's not an admin or
		// verified can only GET their own User entity.
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/v1/user/**").hasAnyAuthority("User", "Verified",
				"Admin");

		// POST Map - only admins or verified users can POST map entities
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/v1/map/**").hasAnyAuthority("Verified", "Admin");
		// DELETE Map - only admins or verified users can POST map entities
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/v1/map/**").hasAnyAuthority("Verified", "Admin");
		// GET Map - all users can get maps
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/v1/map/**").permitAll();

		http.authorizeRequests().anyRequest().authenticated();
		http.addFilter(customAuthenticationFilter);
		http.addFilterBefore(customAuthorisationFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
