package com.sweng22g1.serverapp.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;

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

	@Autowired
	ResourceLoader resourceLoader;

	@PostMapping("map")
	public ResponseEntity<String> uploadMap(@RequestParam("name") String mapName,
			@RequestParam("file") MultipartFile mapFile) {
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

	@GetMapping("map")
	public ResponseEntity<List<Map>> getMaps() {
		log.info("Fetching a list of all maps...");
		return ResponseEntity.ok().body(mapService.getMaps());
	}

	@GetMapping(path = "map/{name}")
	public void getMap(HttpServletRequest request, HttpServletResponse response, @PathVariable("name") String mapName)
			throws IOException {
		File mapFile = new File(mapService.getMap(mapName).getFilepath());
		if (mapFile.exists()) {

			// get the mimetype
			String mimeType = URLConnection.guessContentTypeFromName(mapFile.getName());
			if (mimeType == null) {
				// unknown mimetype so set the mimetype to application/octet-stream
				mimeType = "application/octet-stream";
			}

			response.setContentType(mimeType);

			/**
			 * In a regular HTTP response, the Content-Disposition response header is a
			 * header indicating if the content is expected to be displayed inline in the
			 * browser, that is, as a Web page or as part of a Web page, or as an
			 * attachment, that is downloaded and saved locally.
			 * 
			 */

			/**
			 * Here we have mentioned it to show inline
			 */
			response.setHeader("Content-Disposition", String.format("inline; filename=\"" + mapFile.getName() + "\""));

			// Here we have mentioned it to show as attachment
			// response.setHeader("Content-Disposition", String.format("attachment;
			// filename=\"" + file.getName() + "\""));

			response.setContentLength((int) mapFile.length());

			InputStream inputStream = new BufferedInputStream(new FileInputStream(mapFile));

			FileCopyUtils.copy(inputStream, response.getOutputStream());

		}
	}

}