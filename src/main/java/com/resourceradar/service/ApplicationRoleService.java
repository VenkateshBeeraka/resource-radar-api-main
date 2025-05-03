package com.resourceradar.service;

import com.resourceradar.entity.ApplicationRole;
import com.resourceradar.exception.ApplicationRoleNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ApplicationRoleService {

    List<ApplicationRole> getAllApplicationRoles() throws ApplicationRoleNotFoundException;
    List<ApplicationRole> getApplicationRolesListBasedOnName(String name) throws ApplicationRoleNotFoundException;
    Page<ApplicationRole> getAllApplicationRoles(Pageable pageable) throws ApplicationRoleNotFoundException;
    ApplicationRole getRoleById(String roleId) throws ApplicationRoleNotFoundException;
//    ApplicationRole getRoleByEmployeeId(Long roleId) throws ApplicationRoleNotFoundException;

}
