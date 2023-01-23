package com.sweng22g1.serverapp.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class ServerController {

	/**
	 * @return "pong".
	 * 
	 *         Returns a pong to a ping request, this can be used to check if the
	 *         server is online.
	 */
	@GetMapping("ping")
	public ResponseEntity<String> healthCheck() {
		// Return a message to the console.
		System.out.println("ping pong!");
		// Return a response with status 200 and a body with "pong"
		return ResponseEntity.ok().body("pong");
	}

}
