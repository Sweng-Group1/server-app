package com.sweng22g1.serverapp.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
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


	@Override
	public String toString() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", String.valueOf(this.getId()));
		params.put("username", this.getUsername());
		params.put("firstname", this.getFirstname());
		params.put("lastname", this.getLastname());
		params.put("email", this.getEmail());
//		TODO assess whether password output in toString is necessary
//		Password is hashed before storage, so safety isn't a huge concern I think.
		params.put("password", this.getPassword());
//		TODO add User's Role and Post associations to User.toString()  
//		params.put("roles", this.getRoles().toString());
//		params.put("posts", this.getPosts().toString());
		params.put("created", String.valueOf(this.getCreated()));
		return params.toString();
	}

}
