package com.sweng22g1.serverapp.api;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sweng22g1.serverapp.model.Media;
import com.sweng22g1.serverapp.service.MediaServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sidharth Shanmugam
 * 
 *         The endpoints for the Media entity - this is where external
 *         applications can communicate with the server based on the methods
 *         defined.
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class MediaController {

	private final MediaServiceImpl mediaService;

	@PostMapping("media")
	public ResponseEntity<String> uploadMedia(@RequestParam("mime") String mediaMimeType,
			@RequestParam("file") MultipartFile mediaFile) throws IOException {
		String message = "";
		try {
			log.info("Attempting to handle media upload...");
			Media createdMedia = mediaService.createMedia(mediaFile.getBytes(), mediaMimeType);
			message = "Media has been saved! id: " + String.valueOf(createdMedia.getId());
			return ResponseEntity.ok().body(message);
		} catch (Exception e) {
			log.error("Media upload endpoint fail, exception=" + e.getMessage());
			message = "Map save fail! Exception: " + e.getMessage();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
		}
	}

	// Note from Sid: I'm not implementing a "get a list of all Media uploads"
	// endpoint as that may be a big safety issue. We only want clients to get
	// individual media based on an ID.

	@GetMapping(path = "media/{id}")
	public void getMedia(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Long mediaID)
			throws IOException {
		Media thisMedia = mediaService.getMedia(mediaID);
		if (thisMedia != null) {
			File mediaFile = new File(thisMedia.getFilepath());
			// get the mimetype
			String mimeType = thisMedia.getMimetype();
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
			
			// TODO: This is a bodge to include the filetype. 
			String extension = "";
			if (mimeType == "image/jpeg" || mimeType == "image/jpg") {
				extension = ".jpeg";
			}
			else if (mimeType.equals("image/png")) {
				extension = ".png";
				}
			else if (mimeType.equals("video/mp4")) {
				extension = ".mp4";
			}
			else if (mimeType.equals("audio/mp3")) {
				extension = ".mp3";
			}
			response.setHeader("Content-Disposition", String.format("inline; filename=mediaDownload" + extension));

			// Here we have mentioned it to show as attachment
			// response.setHeader("Content-Disposition", String.format("attachment;
			// filename=\"" + file.getName() + "\""));

			response.setContentLength((int) mediaFile.length());

			InputStream inputStream = new BufferedInputStream(new FileInputStream(mediaFile));

			FileCopyUtils.copy(inputStream, response.getOutputStream());

		} else {
			response.setStatus(NOT_FOUND.value());
		}
	}

	@DeleteMapping(path = "media/{id}")
	public void deleteMedia(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Long mediaID)
			throws IOException {
		try {
			log.info("Deleting media id=" + mediaID);
			mediaService.deleteMedia(mediaID);
			response.setStatus(OK.value());
		} catch (IOException e) {
			log.error("Media delete endpoint fail, exception=" + e.getMessage());
			response.setStatus(INTERNAL_SERVER_ERROR.value());
		}
	}

}
