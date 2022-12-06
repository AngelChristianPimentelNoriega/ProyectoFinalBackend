package com.customer.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.customer.api.dto.ApiResponse;
import com.customer.api.entity.Region;
import com.customer.api.repository.RepoCustomer;
import com.customer.api.repository.RepoRegion;
import com.customer.exception.ApiException;

@Service
public class SvcRegionImp implements SvcRegion {

	@Autowired
	RepoRegion repoRegion;
	
	@Autowired
	RepoCustomer repoCustomer;
	
	@Override
	public List<Region> getRegions() {
		try {
			return repoRegion.findByStatus(1);
		}catch(Exception e) {
			throw new ApiException(HttpStatus.NOT_FOUND, e.getLocalizedMessage());
		}
	}

	@Override
	public Region getRegion(Integer id_region) {
		try {
			return repoRegion.findById(id_region).get();
		}catch(Exception e) {
			throw new ApiException(HttpStatus.NOT_FOUND, e.getLocalizedMessage());
		}
	}

	@Override
	public ApiResponse createRegion(Region region) {
		try {
			Region r = repoRegion.getRegionByName(region.getRegion());
			if(r != null) {
				repoRegion.updateExistingRegion(region.getRegion());
				return new ApiResponse("region updated");
			}
			region.setStatus(1);
			repoRegion.save(region);
			return new ApiResponse("region created");
		}catch(Exception e) {
			if (e instanceof DataIntegrityViolationException) {
				DataIntegrityViolationException ex = (DataIntegrityViolationException) e;
				if (ex.getMostSpecificCause().toString().contains("region"))
					throw new ApiException(HttpStatus.NOT_FOUND, "region alredy exists");
			}
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		}
	}

	@Override
	public ApiResponse updateRegion(Region region, Integer id_region) {
		try {
			Region r = repoRegion.getRegionByName(region.getRegion());
			if(r != null) {
				repoRegion.updateExistingRegion(region.getRegion());
				return new ApiResponse("region updated");
			}
			region.setId_region(id_region);
			region.setStatus(1);
			repoRegion.save(region);
			return new ApiResponse("region updated");
		}catch(Exception e) {
			if (e instanceof DataIntegrityViolationException) {
				DataIntegrityViolationException ex = (DataIntegrityViolationException) e;
				if (ex.getMostSpecificCause().toString().contains("region"))
					throw new ApiException(HttpStatus.NOT_FOUND, "region alredy exists");
			}
			throw new ApiException(HttpStatus.NOT_FOUND, e.getLocalizedMessage());
		}
	}

	@Override
	public ApiResponse deleteRegion(Integer id_region) {
		try {
			if(repoCustomer.findByRegion(id_region).size() != 0)
				throw new ApiException(HttpStatus.BAD_REQUEST, "region cannot be removed if it has clients");
			repoRegion.deleteById(id_region);
			return new ApiResponse("region removed");
		}catch(Exception e) {
			throw new ApiException(HttpStatus.NOT_FOUND, e.getLocalizedMessage());
		}
	}
}