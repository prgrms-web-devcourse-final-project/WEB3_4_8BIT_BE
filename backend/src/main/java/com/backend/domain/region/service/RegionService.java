package com.backend.domain.region.service;

import static com.backend.domain.region.dto.response.RegionResponse.*;

import java.util.List;

public interface RegionService {

	List<Basic> getAllRegions();
}
