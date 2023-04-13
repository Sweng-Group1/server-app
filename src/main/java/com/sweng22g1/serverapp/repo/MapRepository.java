package com.sweng22g1.serverapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sweng22g1.serverapp.model.Map;

public interface MapRepository extends JpaRepository<Map, Long> {

	Map findByName(String name);

	Map findByFilepath(String filepath);

}
