package com.sweng22g1.serverapp.api;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sweng22g1.serverapp.model.Hashtag;
import com.sweng22g1.serverapp.service.HashtagServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sidharth Shanmugam
 * 
 *         The endpoints for the Hashtag entity - this is where external
 *         applications can communicate with the server based on the methods
 *         defined.
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class HashtagController {

	private final HashtagServiceImpl hashtagService;

	@GetMapping("hashtag")
	public ResponseEntity<List<Hashtag>> getHashtags() {
		// all users should be able to fetch hashtags
		log.info("List all hashtags was requested a user");
		return ResponseEntity.ok().body(hashtagService.getHashtags());
	}

	@PostMapping("hashtag")
	public ResponseEntity<Hashtag> updateHashtag(@PathVariable("name") String name,
			@RequestParam(required = false) String newName, @RequestParam(required = false) Double newLatitude,
			@RequestParam(required = false) Double newLongitude, Principal principal) {
		String usernameThatRequested = "";
		// Get the username from the auth token
		usernameThatRequested = principal.getName();
		// Get the post entity that the user wants to edit
		Hashtag hashtagToEdit = hashtagService.getHashtag(name);
		// If the post doesn't exist, log it and return a 404 error.
		if (hashtagToEdit == null) {
			log.warn("User: \"" + usernameThatRequested + "\" attempted to edit a hashtag that does not exist.");
			return ResponseEntity.status(NOT_FOUND).body(null);
		}

		log.info("A successful request was made by user \"" + usernameThatRequested + "\" to update hashtag:" + name);
		if (newLatitude != null && hashtagToEdit.getLatitude() != newLatitude) {
			log.info("Updating latitude");
			hashtagToEdit.setLatitude(newLatitude);
		}
		if (newLongitude != null && hashtagToEdit.getLongitude() != newLongitude) {
			log.info("Updating longitude");
			hashtagToEdit.setLongitude(newLongitude);
		}
		if (newName != null && hashtagToEdit.getName() != newName) {
			log.info("Updating name");
			hashtagToEdit.setName(newName);
		}
		hashtagService.saveHashtag(hashtagToEdit);
		return ResponseEntity.ok().body(hashtagToEdit);

	}

}
