package com.sweng22g1.serverapp.api;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

	@GetMapping("post")
	public ResponseEntity<List<Post>> getPosts(Principal principal) {
		try {
			// We first check if the user is logged in, principal will be null if a user
			// hasn't logged on.
			if (principal != null) {
				// If the user is logged in, we can return a response with all posts
				log.info("List all posts were requested by user: " + principal.getName());
				return ResponseEntity.ok().body(postService.getPosts());
			} else {
				// If the user is not logged on, we only want to return a list of posts from
				// "Verified" or "Admin" accounts.
				// First, instantiate a variable list for the posts to return. I couln't
				// instantiate a List of type Post, so had to run getPosts() to instantiate it,
				// then clear it
				List<Post> verifiedAndAdminPosts = postService.getPosts();
				verifiedAndAdminPosts.clear();
				// First we get all users
				List<User> allUsers = userService.getUsers();
				// Next we iterate through each user to only include the posts from "Admin" or
				// "Verified" accounts
				for (User thisUser : allUsers) {
					if (thisUser.getRoles().stream().anyMatch(p -> p.getName().equals("Admin"))
							|| thisUser.getRoles().stream().anyMatch(p -> p.getName().equals("Verified"))) {
						for (Post verifiedAndAdminPost : thisUser.getPosts()) {
							// This post is appended to the list
							verifiedAndAdminPosts.add(verifiedAndAdminPost);
						}
					}
				}
				// Yes, I know it's really inefficient to list all users, then parse through all
				// of their posts to only append the "Admin" or "Verified" posts. But it's
				// really annoying that Spring creates a table called user_posts that acts as a
				// lookup to link user IDs to their post IDs, but I can't seem to be able to
				// query this table. Maybe in the future I'll be able to update this logic once
				// I figure out how to access this relation table.
				log.info("List all posts requested by logged out user, Admin and Verified posts returned");
				return ResponseEntity.ok().body(verifiedAndAdminPosts);
			}
		} catch (Exception e) {
			log.error("An error was seen trying to list all posts. Exception: " + e.getMessage());
			return ResponseEntity.internalServerError().body(null);
		}
	}

	/**
	 * Endpoint to delete a post. Only user who created that post can delete it,
	 * unless they're an "Admin" or a "Verified", in that case those users can
	 * delete any post regardless of the author. Should return a 200 OK HTTP status
	 * code on successful deletion.
	 */
	@DeleteMapping(path = "post/{id}")
	public void deletePost(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Long postID,
			Principal principal) {

		String usernameThatRequested = "";
		try {
			// Try to get the username of the user trying to make this request and delete
			// the post
			usernameThatRequested = principal.getName();
			// Get the user entity from the username
			User requestingUser = userService.getUser(usernameThatRequested);
			// Check if the requesting user owns this post
			if (requestingUser.getPosts().stream().anyMatch(p -> p.getId().equals(postID))) {
				// If the condition is met, the post can be deleted
				log.info("A successful attempt by user: \"" + usernameThatRequested + "\" to delete their post, id:"
						+ postID);
				postService.deletePost(postID);
				response.setStatus(OK.value());
			} else if (requestingUser.getRoles().stream().anyMatch(p -> p.getName().equals("Admin"))
						|| requestingUser.getRoles().stream().anyMatch(p -> p.getName().equals("Verified"))) {
				// If the previous condition is not met, we want to check whether the user is
				// either an "Admin" or "Verified"
				// If so then post can be deleted
				log.info("A successful attempt by user: \"" + usernameThatRequested + "\" to delete a post, id:"
						+ postID);
				postService.deletePost(postID);
				response.setStatus(OK.value());
			} else {
					// If none of the previous conditions were met, then we block and log this
					// request
					log.warn("A attempt by user: \"" + usernameThatRequested + "\" to delete a post, id:" + postID
							+ " was blocked!");
					response.setStatus(FORBIDDEN.value());
			}
	
		} catch (Exception e) {
			// An exception will be raised if the username of the user making this request
			// could not be found. We need to block and log the request if it was made by a
			// logged out user.
			log.warn("An attempt by a logged out user to delete a post id: \"" + postID + "\" was blocked!");
			response.setStatus(FORBIDDEN.value());
		}
	}

}
