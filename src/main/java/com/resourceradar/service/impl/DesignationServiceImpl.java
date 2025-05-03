package com.resourceradar.service.impl;

import java.util.List;

import com.resourceradar.entity.Designation;
import com.resourceradar.repository.DesignationRepository;
import com.resourceradar.service.DesignationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.resourceradar.exception.DesignationNotFoundException;

@Service
public class DesignationServiceImpl implements DesignationService {

	@Autowired
	private DesignationRepository designationRepository;

	@Override
	public List<Designation> getAllDesignation() throws DesignationNotFoundException {
		try {
			List<Designation> designations = designationRepository.findAll();
			return designations;
		} catch (Exception e) {
			throw new DesignationNotFoundException(" Desigantion Details not found");
		}
	}

	@Override
	public List<Designation> getDesignationListBasedOnName(String name) {

		List<Designation> designations = designationRepository.findByNameContainingIgnoreCase(name);
		return designations;


	}

	@Override
	public Page<Designation> getAllDesignation(Pageable pageable) throws DesignationNotFoundException {
		try {
			return designationRepository.findAll(pageable);
		} catch (Exception e) {
			throw new DesignationNotFoundException("Designation details not found");
		}
	}
}
