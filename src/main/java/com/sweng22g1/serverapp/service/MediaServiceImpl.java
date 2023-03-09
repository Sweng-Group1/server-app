package com.sweng22g1.serverapp.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.sweng22g1.serverapp.model.Media;
import com.sweng22g1.serverapp.repo.MediaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MediaServiceImpl implements MediaService {

	private final MediaRepository mediaRepo;
	String RESOURCES_DIR = MediaServiceImpl.class.getResource("/").getPath();

	@Override
	public Media createMedia(byte[] mediaBytes) throws IOException {
		Media thisMedia = Media.builder().filepath("").build(); // create entity
		log.info("Saving Media \"{}\" to the db...", thisMedia.getId());
		Path newFile = Paths.get(RESOURCES_DIR + "media/" + thisMedia.getId()); // instantiate directory
		Files.createDirectories(newFile.getParent()); // create folders if they don't exist
		Files.write(newFile, mediaBytes);	// write the file to the directory
		thisMedia.setFilepath(newFile.toAbsolutePath().toString());	// set entity filepath field
		return mediaRepo.save(thisMedia);
	}

	@Override
	public Media deleteMedia(Long id) throws IOException {
		Optional<Media> thisMediaOptional = mediaRepo.findById(id);
		if (thisMediaOptional.isPresent()) {
			log.info("Deleting Media \"{}\"...", id);
			Media thisMedia = thisMediaOptional.get();	// Get the media entity to delete
			Files.deleteIfExists(Paths.get(thisMedia.getFilepath())); // Delete the file of the entity
			mediaRepo.delete(thisMedia);	// delete the entity from db
		} else {
			log.info("Couldn't find Media \"{}\" for deletion", id);
		}
		return null;
	}

	@Override
	public Media getMedia(Long id) {
		log.info("Retrieving Media \"{}\"", id);
		return mediaRepo.findById(id).get();
	}

	@Override
	public List<Media> getMedia() {
		log.info("Retrieving all Media...");
		return mediaRepo.findAll();
	}

}
