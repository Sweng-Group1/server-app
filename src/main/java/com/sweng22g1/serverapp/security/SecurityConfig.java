package com.sweng22g1.serverapp.security;

import org.springframework.context.annotation.Configuration;
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

@Configuration
@EnableWebSecurity
@SuppressWarnings("deprecation")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
		customAuthenticationFilter.setFilterProcessesUrl("/api/v1/login");
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.authorizeRequests().antMatchers("/api/v1/ping").permitAll();
		http.authorizeRequests().antMatchers("/api/v1/login/**", "/api/v1/token/refresh/**").permitAll();
//		TODO: Add auth rules to endpoints once endpoints have been added!
//		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/v1/user/**").hasAnyAuthority("User");
//		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/v1/role/**").hasAnyAuthority("Admin", "SuperAdmin");
//		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/v1/user/**").hasAnyAuthority("Admin", "SuperAdmin");
//		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/v1/role/**").hasAnyAuthority("Admin");
		http.authorizeRequests().anyRequest().authenticated();
		http.addFilter(customAuthenticationFilter);
		http.addFilterBefore(customAuthorisationFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
