package com.sweng22g1.serverapp.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100, unique = true)
	private String username;

	@Column(nullable = false, length = 100)
	private String firstname;

	@Column(nullable = false, length = 100)
	private String lastname;

	@Column(nullable = false, length = 100)
	private String email;

	@Column(nullable = false, length = 100)
	private String password;

	@CreationTimestamp
	private Date created;

//	TODO create Role model for many-to-many relationship with User 
//	@ManyToMany(fetch = FetchType.EAGER)
//	private Collection<Role> roles = new ArrayList<>();

//	TODO create Post model for one-to-many relationship with User
//	@OneToMany(fetch = FetchType.EAGER)
//	private Collection<Post> posts = new ArrayList<>();

	/**
	 * @return the id
	 */
	private Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	private void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the username
	 */
	private String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	private void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the firstname
	 */
	private String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname the firstname to set
	 */
	private void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return the lastname
	 */
	private String getLastname() {
		return lastname;
	}

	/**
	 * @param lastname the lastname to set
	 */
	private void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * @return the email
	 */
	private String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	private void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	private String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	private void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the created
	 */
	private Date getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	private void setCreated(Date created) {
		this.created = created;
	}

	public User(Long id, String username, String firstname, String lastname, String email, String password,
			Date created) {
		this.id = id;
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.password = password;
		this.created = created;
	}

	public User() {
	}

}
