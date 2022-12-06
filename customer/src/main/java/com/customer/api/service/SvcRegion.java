package com.customer.api.service;

import java.util.List;

import com.customer.api.dto.ApiResponse;
import com.customer.api.entity.Region;

public interface SvcRegion {

	public List<Region> getRegions();
	public Region getRegion(Integer id_region);
	public ApiResponse createRegion(Region region);
	public ApiResponse updateRegion(Region region, Integer id_region);
	public ApiResponse deleteRegion(Integer id_region);
}