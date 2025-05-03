package com.resourceradar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.resourceradar.entity.ApplicationRole;
import com.resourceradar.exception.ApplicationRoleNotFoundException;
import com.resourceradar.repository.ApplicationRoleRepository;
import com.resourceradar.service.ApplicationRoleService;
@Service
public class ApplicationRoleServiceImpl implements ApplicationRoleService {

    @Autowired
    private ApplicationRoleRepository applicationRoleRepository;
    @Override
    public List<ApplicationRole> getAllApplicationRoles() throws ApplicationRoleNotFoundException {
        try {
            return applicationRoleRepository.findAll();
        } catch (Exception e) {
            throw new ApplicationRoleNotFoundException("Application roles not found");
        }
    }
    @Override
    public List<ApplicationRole> getApplicationRolesListBasedOnName(String name) throws ApplicationRoleNotFoundException {
        try {
            List<ApplicationRole> roles = applicationRoleRepository.findByNameContainingIgnoreCase(name);
            return roles;
        } catch (Exception e) {
            throw new ApplicationRoleNotFoundException("Application roles not found");
        }
    }
    @Override
    public Page<ApplicationRole> getAllApplicationRoles(Pageable pageable) throws ApplicationRoleNotFoundException {
        try {
            return applicationRoleRepository.findAll(pageable);
        } catch (Exception e) {
            throw new ApplicationRoleNotFoundException("Application roles not found");
        }
    }

    @Override
    public ApplicationRole getRoleById(String roleId) throws ApplicationRoleNotFoundException {
        Optional<ApplicationRole> optionalRole = applicationRoleRepository.findById(roleId);
        return optionalRole.orElseThrow(() -> new ApplicationRoleNotFoundException("Application role not found with ID: " + roleId));
    }
}
