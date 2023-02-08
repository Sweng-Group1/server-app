package com.sweng22g1.serverapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sweng22g1.serverapp.model.Media;

public interface MediaRepository extends JpaRepository<Media, Long> {
	
	Media findByFilepath(String filepath);

}
