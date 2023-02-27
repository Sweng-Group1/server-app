package com.sweng22g1.serverapp.service;

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

	@Override
	public Map saveMap(Map map) {
		log.info("Saving Map \"{}\" to the db...", map.getName());
		return mapRepo.save(map);
	}

	@Override
	public Map deleteMap(String mapName) {
		log.info("Deleting Map \"{}\" from the db...", mapName);
		Map thisMap = mapRepo.findByName(mapName);
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
