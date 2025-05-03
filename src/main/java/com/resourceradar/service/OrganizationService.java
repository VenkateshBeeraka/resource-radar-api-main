package com.resourceradar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.resourceradar.dto.OrganizationDTO;
import com.resourceradar.entity.Organization;

@Service
public interface OrganizationService {

	public List<Organization> getAllOrganization();
	
	public OrganizationDTO getOrgCompleteDetails(String request);

}
