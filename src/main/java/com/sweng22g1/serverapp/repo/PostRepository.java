package com.sweng22g1.serverapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sweng22g1.serverapp.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

}
