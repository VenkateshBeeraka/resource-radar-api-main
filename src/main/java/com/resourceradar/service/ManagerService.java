package com.resourceradar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.resourceradar.dto.ManagerDto;

@Service
public interface ManagerService {
	 
	public List<ManagerDto> getAllManagers();
	
	
	
}
