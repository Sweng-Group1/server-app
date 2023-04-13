package com.sweng22g1.serverapp.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.sweng22g1.serverapp.model.Map;
import com.sweng22g1.serverapp.repo.MapRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MapServiceImpl implements MapService {
	
	private final MapRepository mapRepo;
	String RESOURCES_DIR = MapServiceImpl.class.getResource("/").getPath();
	
	@Override
	public Map createMap(String name, byte[] mapBytes) throws IOException {
		Map thisMap = Map.builder().name(name).filepath("/").build(); // create entity in db
		mapRepo.save(thisMap);
		log.info("Saving Map \"{}\" to the db and fs...", thisMap.getId());
		Path newFile = Paths.get(RESOURCES_DIR + "maps/" + thisMap.getId()); // instantiate filepath
		Files.createDirectories(newFile.getParent());	// create directories in fs if they don't exist
		Files.write(newFile, mapBytes);	// write the file to fs
		thisMap.setFilepath(newFile.toAbsolutePath().toString());	// set entity filepath field
		return mapRepo.save(thisMap);
	}
	
	@Override
	public Map deleteMap(String mapName) throws IOException {
		Map thisMap = mapRepo.findByName(mapName);	// find the entity to delete
		log.info("Deleting Map \"{}\"...", mapName);
		Files.deleteIfExists(Paths.get(thisMap.getFilepath())); // delete the fs file if exists
		mapRepo.delete(thisMap);	// delete the entity from the db
		return null;
	}

	@Override
	public Map getMap(String mapName) {
		log.info("Retrieving Map \"{}\"", mapName);
		return mapRepo.findByName(mapName);
	}

	@Override
	public List<Map> getMaps() {
		log.info("Retrieving all Maps...");
		return mapRepo.findAll();
	}

}
