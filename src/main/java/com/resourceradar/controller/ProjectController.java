package com.resourceradar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.resourceradar.config.EndPointConfig;
import com.resourceradar.dto.ProjectDTO;
import com.resourceradar.dto.ProjectGetDTOResponse;
import com.resourceradar.exception.DeleteResponse;
import com.resourceradar.exception.OrgIdNotFoundException;
import com.resourceradar.exception.ProjectNotFoundException;
import com.resourceradar.exception.ResourceNotFoundException;
import com.resourceradar.service.ProjectService;
import com.resourceradar.utils.Constants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping(EndPointConfig.API_V1 + EndPointConfig.PROJECT_DETAILS)
@Tag(name = "project")
public class ProjectController {
	
	@Autowired
	public ProjectService projectService;


	@PostMapping()
	@PreAuthorize("hasRole('ROLE_HR') OR hasRole('ROLE_ResourceManager') OR hasRole('ROLE_Admin')")
	@Operation(
            summary = "Create a new Project",
            description = "Create a new Project",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER,
                            name = "X-Org-Id",
                            description = "Organization Id",
                            required = true,
                            schema = @Schema(type = "string"))},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = ProjectDTO.class))
                                    )
                            }
                    ),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO, HttpServletRequest request) throws OrgIdNotFoundException, ResourceNotFoundException {
		ProjectDTO createdProject = projectService.createProject(projectDTO, request.getHeader(Constants.ORG_ID));
		if (createdProject == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(createdProject);
	}
	
	@PutMapping("/{projectId}")
	@PreAuthorize("hasRole('ROLE_HR') OR hasRole('ROLE_ResourceManager') OR hasRole('ROLE_Admin')")
	@Operation(
            summary = "update a new Project",
            description = "update a new Project",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER,
                            name = "X-Org-Id",
                            description = "Organization Id",
                            required = true,
                            schema = @Schema(type = "string"))},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = ProjectDTO.class))
                                    )
                            }
                    ),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
	public ResponseEntity<ProjectDTO> updateProject(@PathVariable String projectId, @RequestBody ProjectDTO project, HttpServletRequest request) throws ProjectNotFoundException, OrgIdNotFoundException, ResourceNotFoundException {
		ProjectDTO existingProject = projectService.updateProject(projectId, project, request.getHeader(Constants.ORG_ID));
		if (existingProject == null) {
				return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(existingProject);
	}
		
	@GetMapping("/{projectId}")
	@PreAuthorize("hasRole('ROLE_HR') OR hasRole('ROLE_ResourceManager') OR hasRole('ROLE_Admin')")
    @Transactional
    @Operation(
            summary = "Find a projects by Id",
            description = "Find a projects by Id.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectDTO.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<ProjectGetDTOResponse> getProjectById(@PathVariable String projectId) throws ProjectNotFoundException {
    	ProjectGetDTOResponse project = projectService.getProjectById(projectId);
        if (project == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

	 @GetMapping
	 @PreAuthorize("hasRole('ROLE_HR') OR hasRole('ROLE_ResourceManager') OR hasRole('ROLE_Admin')")
	    @Operation(
	            summary = "Retrieve a list of all projects",
	            description = "Retrieve a list of all projects.",
	            responses = {
	                    @ApiResponse(
	                            description = "Success",
	                            responseCode = "200",
	                            content = {
	                                    @Content(
	                                            mediaType = "application/json",
	                                            array = @ArraySchema(schema = @Schema(implementation = ProjectDTO.class))
	                                    )
	                            }
	                    ),
	                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
	            }
	    )
		public ResponseEntity<List<ProjectGetDTOResponse>> getAllProjects() {
        List<ProjectGetDTOResponse> projects = projectService.getAllProjects();
		if (projects.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(projects, HttpStatus.OK);
		}
	}
	
	@DeleteMapping(EndPointConfig.ID)
    @Operation(
            summary = "Deletes a project",
            description = "Deletes an project by Id.",
            responses = {
                    @ApiResponse(description = "Deleted", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<String> deleteProject(@PathVariable("id") String id) throws ProjectNotFoundException, DeleteResponse {
        String deleteProject = projectService.deleteProject(id);
        if (deleteProject == null) {
        	return ResponseEntity.notFound().build();
	}
        throw new DeleteResponse(deleteProject);
}
}
