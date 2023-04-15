package com.sweng22g1.serverapp.api;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sweng22g1.serverapp.model.Post;
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

}
