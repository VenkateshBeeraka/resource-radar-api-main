package com.resourceradar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.resourceradar.dto.ProjectDTO;
import com.resourceradar.dto.ProjectGetDTOResponse;
import com.resourceradar.exception.OrgIdNotFoundException;
import com.resourceradar.exception.ProjectNotFoundException;
import com.resourceradar.exception.ResourceNotFoundException;

@Service
public interface ProjectService {

	ProjectDTO createProject(ProjectDTO projectDTO, String orgId)
			throws OrgIdNotFoundException, ResourceNotFoundException;

	ProjectDTO updateProject(String projectId, ProjectDTO project, String orgId)
			throws ProjectNotFoundException, OrgIdNotFoundException, ResourceNotFoundException;

	List<ProjectGetDTOResponse> getAllProjects();

	ProjectGetDTOResponse getProjectById(String projectId) throws ProjectNotFoundException;

	String deleteProject(String id) throws ProjectNotFoundException;

	boolean isValidProject(String projectId);
}
