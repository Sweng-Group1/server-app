package com.sweng22g1.serverapp.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
