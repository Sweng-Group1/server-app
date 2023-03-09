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
		Map thisMap = Map.builder().name(name).filepath("").build();
		log.info("Saving Map \"{}\" to the db and fs...", thisMap.getId());
		Path newFile = Paths.get(RESOURCES_DIR + "maps/" + thisMap.getId());
		Files.createDirectories(newFile.getParent());
		Files.write(newFile, mapBytes);
		thisMap.setFilepath(newFile.toAbsolutePath().toString());
		return mapRepo.save(thisMap);
	}
	
	@Override
	public Map deleteMap(String mapName) throws IOException {
		Map thisMap = mapRepo.findByName(mapName);
		log.info("Deleting Map \"{}\"...", mapName);
		Files.deleteIfExists(Paths.get(thisMap.getFilepath()));
		mapRepo.delete(thisMap);
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
