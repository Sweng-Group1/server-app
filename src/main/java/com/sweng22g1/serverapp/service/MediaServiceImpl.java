package com.sweng22g1.serverapp.service;

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

	@Override
	public Media saveMedia(Media media) {
		log.info("Saving Media \"{}\" to the db...", media.getId());
		return mediaRepo.save(media);
	}

	@Override
	public Media deleteMedia(Long id) {
		Optional<Media> thisMediaOptional = mediaRepo.findById(id);
		if (thisMediaOptional.isPresent()) {
			log.info("Deleting Media \"{}\"...", id);
			Media thisMedia = thisMediaOptional.get();
			mediaRepo.delete(thisMedia);
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
