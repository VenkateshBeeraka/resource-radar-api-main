package com.resourceradar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.resourceradar.config.EndPointConfig;
import com.resourceradar.dto.OrganizationDTO;
import com.resourceradar.entity.Organization;
import com.resourceradar.service.impl.OrganizationServiceImpl;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EndPointConfig.API_V1)
@Slf4j
@Tag(name = "organization")
public class OrganizationController {

	@Autowired
	private OrganizationServiceImpl organizationService;
	
	@Autowired
	private OrganizationServiceImpl organizationServiceImpl;

		

	@GetMapping(EndPointConfig.ORGANIZATION_DETAILS)
	public ResponseEntity<List<Organization>> getAllOrganizations() {
		List<Organization> organizations = organizationService.getAllOrganization();

		if (organizations.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(organizations, HttpStatus.OK);
		}
	}

	@GetMapping("/getorg")
	public  ResponseEntity<OrganizationDTO>  getOrgCompleteDetails(String request) {
		
		
		OrganizationDTO organizationDTO = organizationServiceImpl.getOrgCompleteDetails(request);
		return  new ResponseEntity<OrganizationDTO>(organizationDTO, HttpStatus.ACCEPTED);
	}
}
	
	
