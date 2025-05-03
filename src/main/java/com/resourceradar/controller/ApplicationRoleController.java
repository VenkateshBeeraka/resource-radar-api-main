package com.resourceradar.controller;


import com.resourceradar.config.EndPointConfig;
import com.resourceradar.entity.ApplicationRole;
import com.resourceradar.exception.ApplicationRoleNotFoundException;
import com.resourceradar.service.impl.ApplicationRoleServiceImpl;
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

import java.util.List;

@RestController
@RequestMapping(EndPointConfig.API_V1)
@Tag(name = "roles")
public class ApplicationRoleController {

    @Autowired
    private ApplicationRoleServiceImpl applicationRoleService;

    @GetMapping(EndPointConfig.APPLICATIONROLE)
    public ResponseEntity<List<ApplicationRole>> getAllApplicationRoles() throws ApplicationRoleNotFoundException {
        List<ApplicationRole> roles = applicationRoleService.getAllApplicationRoles();

        if (roles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(roles, HttpStatus.OK);
        }
    }

    @GetMapping(EndPointConfig.APPLICATIONROLE_SEARCH)
    public ResponseEntity<List<ApplicationRole>> searchApplicationRolesBasedOnName(@RequestParam("query") String query)
            throws ApplicationRoleNotFoundException {
        List<ApplicationRole> roles = applicationRoleService.getApplicationRolesListBasedOnName(query);

        if (roles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(roles, HttpStatus.OK);
        }
    }

    public ResponseEntity<Page<ApplicationRole>> getAllApplicationRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws ApplicationRoleNotFoundException {
        Pageable pageable = PageRequest.of(page, size);
        Page<ApplicationRole> roles = applicationRoleService.getAllApplicationRoles(pageable);

		if (roles.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(roles, HttpStatus.OK);
		}
	}
}
