package com.sweng22g1.serverapp.api;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sweng22g1.serverapp.model.Post;
import com.sweng22g1.serverapp.model.Role;
import com.sweng22g1.serverapp.model.User;
import com.sweng22g1.serverapp.service.PostServiceImpl;
import com.sweng22g1.serverapp.service.UserServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class PostController {

	private final PostServiceImpl postService;
	private final UserServiceImpl userService;

	@PostMapping("post")
	public ResponseEntity<Post> createPost(@RequestParam String xmlContent, @RequestParam int validityHours,
			@RequestParam Double latitude, @RequestParam Double longitude, Principal principal) {

		// Anyone that has an account ("User", "Verfied", or "Admin") can create posts.
		// This auth rule has already been specified in the SecurityConfig, so nothing
		// much is needed here logic-wise. However, we still need to know which user has
		// requested to create a new post, as the user entity has a OneToMany
		// relationship with the post entity.

		// Assume user is already logged in with auth token at this point (otherwise
		// blocked by SecurityConfig) so no need for error handling.
		String usernameThatRequested = principal.getName();

		LocalDateTime postExpiry = LocalDateTime.now().plusHours(validityHours);

		// Instantiate new post entity and save
		Post newPost = Post.builder().xmlContent(xmlContent).expiry(postExpiry).latitude(latitude).longitude(longitude)
				.build();
		postService.savePost(newPost);

		// Add this post to the set of posts relating to the user entity
		userService.addPostToUser(usernameThatRequested, newPost.getId());

		// Return a 200 with the new post entity on successful creation
		log.info("Created new post");
		return ResponseEntity.ok().body(newPost);

	}

	// We don't want users to edit the expiry or created timestamps, as this can be
	// used to exploit the system.
	@PostMapping("post/{id}")
	public ResponseEntity<Post> updatePost(@RequestParam(required = false) String newXmlContent,
			@RequestParam(required = false) Double newLatitude, @RequestParam(required = false) Double newLongitude,
			@PathVariable("id") Long id, Principal principal) {

		String usernameThatRequested = "";
		// Get the username from the auth token
		usernameThatRequested = principal.getName();
		// Get the user entity from the username
		User requestingUser = userService.getUser(usernameThatRequested);
		// Get a list of posts that the user owns
		Set<Post> postsByUserRequesting = requestingUser.getPosts();
		// Get the roles of the user requesting this
		Set<Role> requestingUserRoles = requestingUser.getRoles();
		// Get the post entity that the user wants to edit
		Post postToEdit = postService.getPost(id);
		// If the post doesn't exist, log it and return a 404 error.
		if (postToEdit == null) {
			log.warn("User: \"" + usernameThatRequested + "\" attempted to edit a post that does not exist.");
			return ResponseEntity.status(NOT_FOUND).body(null);
		}
		// Only continue if the user is an "Admin", "Verified", or is the owner of the
		// post that needs to be edited.
		if (requestingUserRoles.stream().anyMatch(p -> p.getName().equals("Admin"))
				|| requestingUserRoles.stream().anyMatch(p -> p.getName().equals("Verified"))
				|| postsByUserRequesting.stream().anyMatch(p -> p.getId().equals(id))) {
			log.info("A successful request was made by user \"" + usernameThatRequested + "\" to update post:" + id);
			if (newXmlContent != null && postToEdit.getXmlContent() != newXmlContent) {
				log.info("Updating XML content");
				postToEdit.setXmlContent(newXmlContent);
				postToEdit.setUpdated(LocalDateTime.now());
			}
			if (newLatitude != null && postToEdit.getLatitude() != newLatitude) {
				log.info("Updating latitude");
				postToEdit.setLatitude(newLatitude);
				postToEdit.setUpdated(LocalDateTime.now());
			}
			if (newLongitude != null && postToEdit.getLongitude() != newLongitude) {
				log.info("Updating longitude");
				postToEdit.setLongitude(newLongitude);
				postToEdit.setUpdated(LocalDateTime.now());
			}
			postService.savePost(postToEdit);
			return ResponseEntity.ok().body(postToEdit);
		}
		// Code will only get to this point if the user requesting to make edits is not
		// "Admin", "Verified", or the owner of the post. This needs to be logged and
		// blocked.
		log.warn("Request was made by user \"" + usernameThatRequested + "\" to edit a post was blocked.");
		return ResponseEntity.status(FORBIDDEN).body(null);
	}

}
