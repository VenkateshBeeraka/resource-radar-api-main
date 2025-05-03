package com.resourceradar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.resourceradar.config.EndPointConfig;
import com.resourceradar.dto.ManagerDto;
import com.resourceradar.service.ManagerService;

@RestController
@RequestMapping(EndPointConfig.API_V1 + EndPointConfig.MANAGERS)
public class ManagerController {

	@Autowired
	private ManagerService managerService;

	@GetMapping()
	public List<ManagerDto> getAllManagers() {
		List<ManagerDto> allManagers = managerService.getAllManagers();
		return allManagers;
	}

}
