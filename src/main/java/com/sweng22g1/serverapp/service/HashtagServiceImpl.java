package com.sweng22g1.serverapp.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.sweng22g1.serverapp.model.Hashtag;
import com.sweng22g1.serverapp.repo.HashtagRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sidharth Shanmugam
 * 
 *         CRUD operations defined here, implementing the base Service script.
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HashtagServiceImpl implements HashtagService {

	private final HashtagRepository hashtagRepo;

	@Override
	public Hashtag saveHashtag(Hashtag hashtag) {
		log.info("Saving Hashtag \"{}\" to the db...", hashtag.getName());
		return hashtagRepo.save(hashtag);
	}

	@Override
	public Hashtag deleteHashtag(String hashtagName) {
		log.info("Deleting Hashtag \"{}\" to the db...", hashtagName);
		Hashtag thisHashtag = hashtagRepo.findByName(hashtagName);
		hashtagRepo.delete(thisHashtag);
		return null;
	}

	@Override
	public Hashtag getHashtag(String hashtagName) {
		log.info("Retrieving Hashtag \"{}\"", hashtagName);
		return hashtagRepo.findByName(hashtagName);
	}

	@Override
	public List<Hashtag> getHashtags() {
		log.info("Retrieving all Hashtags...");
		return hashtagRepo.findAll();
	}

}
