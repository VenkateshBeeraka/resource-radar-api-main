package com.resourceradar.controller;

import java.util.List;

import com.resourceradar.config.EndPointConfig;
import com.resourceradar.entity.Designation;
import com.resourceradar.service.impl.DesignationServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.resourceradar.exception.DesignationNotFoundException;

@RestController
@RequestMapping(EndPointConfig.API_V1)
@Tag(name = "designations")
public class DesignationController {

	@Autowired
	private DesignationServiceImpl designationService;

	@GetMapping(EndPointConfig.DESIGNATION)
	public ResponseEntity<List<Designation>> getAllDesignation() throws DesignationNotFoundException {
		List<Designation> designations = designationService.getAllDesignation();

		if (designations.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(designations, HttpStatus.OK);
		}
	}

	@GetMapping(EndPointConfig.DESIGNATION_DETAILS_SEARCH)
	public ResponseEntity<List<Designation>> searchDesignationBasedOnName(@RequestParam("query") String query)
			throws DesignationNotFoundException {
		List<Designation> skills = designationService.getDesignationListBasedOnName(query);

		if (skills.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(skills, HttpStatus.OK);
		}

	}
	public ResponseEntity<Page<Designation>> getAllDesignation(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	) throws DesignationNotFoundException {
		Pageable pageable = PageRequest.of(page, size);
		Page<Designation> designations = designationService.getAllDesignation(pageable);

		if (designations.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(designations, HttpStatus.OK);
		}
	}

}
