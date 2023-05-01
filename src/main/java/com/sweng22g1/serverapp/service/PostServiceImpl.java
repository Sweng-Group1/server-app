package com.sweng22g1.serverapp.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.sweng22g1.serverapp.model.Post;
import com.sweng22g1.serverapp.repo.PostRepository;

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
public class PostServiceImpl implements PostService {

	private final PostRepository postRepo;

	@Override
	public Post savePost(Post post) {
		log.info("Saving Post \"{}\" to the db...", post.getId());
		return postRepo.save(post);
	}

	@Override
	public Post deletePost(Long id) {
		// getting optional type as the post requested for deletion may not exist.
		Optional<Post> thisPostOptional = postRepo.findById(id);
		if (thisPostOptional.isPresent()) {
			// If exists log and delete
			log.info("Deleting Post \"{}\"...", id);
			Post thisPost = thisPostOptional.get();
			postRepo.delete(thisPost);
		} else {
			// If not exists, then log
			log.info("Couldn't find Post \"{}\" for deletion", id);
		}
		return null;
	}

	@Override
	public Post getPost(Long id) {
		log.info("Retrieving Post \"{}\"", id);
		return postRepo.findById(id).get();
	}

	@Override
	public List<Post> getPosts() {
		log.info("Retrieving all Posts...");
		return postRepo.findAll();
	}

}
