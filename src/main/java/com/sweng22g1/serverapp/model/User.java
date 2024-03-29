package com.sweng22g1.serverapp.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sidharth Shanmugam
 *
 *         The User entity which is defined as a table in the database by Spring
 *         Boot and JPA.
 *
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Username cannot be null")
	@Size(max = 100, min = 1, message = "Username length invalid.")
	@Column(unique = true)
	private String username;

	@NotNull(message = "Firstname cannot be null")
	@Size(max = 100, min = 1, message = "Firstname length invalid.")
	private String firstname;

	@NotNull(message = "Lastname cannot be null")
	@Size(max = 100, min = 1, message = "Lastname length invalid.")
	private String lastname;

	@NotNull(message = "Email cannot be null")
	@Size(max = 100, min = 1, message = "Email length invalid.")
	private String email;

	@NotNull(message = "Password cannot be null")
	@Size(min = 8, message = "Password length too small.")
	private String password;

	@CreationTimestamp
	private LocalDateTime created;

	@Builder.Default
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	private Set<Role> roles = new HashSet<Role>();

	@Builder.Default
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE })
	private Set<Post> posts = new HashSet<Post>();

	@Override
	public String toString() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", String.valueOf(this.getId()));
		params.put("username", this.getUsername());
		params.put("firstname", this.getFirstname());
		params.put("lastname", this.getLastname());
		params.put("email", this.getEmail());
		if (this.getRoles() == null) {
			params.put("roles", "null");
		} else {
			params.put("roles", this.getRoles().toString());
		}

		if (this.getPosts() == null) {
			params.put("posts", "null");
		} else {
			params.put("posts", this.getPosts().toString());
		}
		params.put("created", String.valueOf(this.getCreated()));
		return params.toString();
	}

}
