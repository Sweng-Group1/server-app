package com.sweng22g1.serverapp.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sweng22g1.serverapp.model.Map;
import com.sweng22g1.serverapp.service.MapServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class MapController {

	private final MapServiceImpl mapService;

	@PostMapping("map")
	public ResponseEntity<String> uploadMap(@RequestParam("name") String mapName, @RequestParam("file") MultipartFile mapFile) {
		String message = "";
		try {
			log.info("Attempting to handle map upload, name=" + mapName);
			Map createdMap = mapService.createMap(mapName, mapFile.getBytes());
			message = "Map has been saved! Name: " + mapName + " | id: " + String.valueOf(createdMap.getId());
			return ResponseEntity.ok().body(message);
		} catch (Exception e) {
			log.error("Map upload endpoint fail, exception=" + e.getMessage());
			message = "Map save fail! Name: " + mapName + " | exception: " + e.getMessage();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
		}
	}

}
