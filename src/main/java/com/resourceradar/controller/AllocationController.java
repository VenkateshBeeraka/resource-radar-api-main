package com.resourceradar.controller;

import com.resourceradar.config.EndPointConfig;
import com.resourceradar.dto.AllocationRequest;
import com.resourceradar.dto.AllocationResponse;
import com.resourceradar.exception.DeleteResponse;
import com.resourceradar.exception.EmployeeAllocationNotFoundException;
import com.resourceradar.service.AllocationService;
import com.resourceradar.service.EmployeeService;
import com.resourceradar.service.ProjectService;
import com.resourceradar.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.links.LinkParameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(EndPointConfig.API_V1 + EndPointConfig.ALLOCATIONS)
@Tag(name = "Employee, Project Allocation")
public class AllocationController {

    private final AllocationService allocationService;
    private final EmployeeService employeeService;
    private final ProjectService projectService;

    @Autowired
    public AllocationController(AllocationService allocationService, EmployeeService employeeService, ProjectService projectService) {
        this.allocationService = allocationService;
        this.employeeService = employeeService;
        this.projectService = projectService;
    }

    @PostMapping
    @Operation(
            summary = "Create a new allocation",
            description = "Create a new allocation",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "X-Org-Id", description = "Organization Id", required = true, schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = AllocationResponse.class))
                                    )
                            }
                    ),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<?> createAllocation(@RequestBody AllocationRequest allocationRequest, HttpServletRequest request) throws EmployeeAllocationNotFoundException {
        if (!employeeService.isValidEmployee(allocationRequest.getEmployeeId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid employee ID.");
        }

        if (!projectService.isValidProject(allocationRequest.getProjectId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid project ID.");
        }

        if (allocationService.isDuplicateAllocation(allocationRequest.getEmployeeId(), allocationRequest.getProjectId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Allocation already exists.");
        }

        AllocationResponse createdAllocation = allocationService.createAllocation(allocationRequest, request.getHeader(Constants.ORG_ID));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAllocation);
    }

    @PutMapping(EndPointConfig.ID)
    @Operation(
            summary = "Update details of a specific allocation",
            description = "Updates allocation information by passing in the allocation Id and a JSON representation of the updated allocation.",
            responses = {
                    @ApiResponse(
                            description = "Updated",
                            responseCode = "200",
                            links = @Link(name = "get", operationId = "get", parameters = @LinkParameter(name = "id", expression = "$request.body.id")),
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AllocationResponse.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<?> updateEmployeeAllocation(@PathVariable("id") String id, @RequestBody AllocationRequest allocationRequest) throws EmployeeAllocationNotFoundException {
        if (!employeeService.isValidEmployee(allocationRequest.getEmployeeId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid employee ID.");
        }

        if (!projectService.isValidProject(allocationRequest.getProjectId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid project ID.");
        }

        AllocationResponse allocation = allocationService.updateAllocation(id, allocationRequest);
        if (allocation == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(allocation);
    }

    @GetMapping
    @Operation(
            summary = "Retrieve a list of all allocations",
            description = "Retrieve a list of all allocations.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = AllocationResponse.class))
                                    )
                            }
                    ),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<List<AllocationResponse>> getAllEmployeeAllocations() {
        List<AllocationResponse> allocations = allocationService.getAllAllocations();
        return ResponseEntity.ok(allocations);
    }

    @GetMapping(EndPointConfig.ID)
    @Transactional
    @Operation(
            summary = "Finds an allocation",
            description = "Finds an allocation by Id.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AllocationResponse.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<AllocationResponse> getAllocationById(@PathVariable("id") String id) {
        AllocationResponse allocation = allocationService.getAllocationById(id);
        if (allocation == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(allocation);
    }

    @DeleteMapping(EndPointConfig.ID)
    @Operation(
            summary = "Deletes an allocation",
            description = "Deletes an allocation by Id.",
            responses = {
                    @ApiResponse(description = "Deleted", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<String> deleteEmployeeAllocation(@PathVariable("id") String id) throws EmployeeAllocationNotFoundException, DeleteResponse {
        String allocation = allocationService.deleteEmployeeAllocation(id);
        if (allocation == null) {
        	return ResponseEntity.notFound().build();
	}
        throw new DeleteResponse(allocation);
    }

    @GetMapping(EndPointConfig.ALLOCATIONS_BY_PROJECT)
    @Transactional
    @Operation(
            summary = "Finds all allocations for a specific project",
            description = "Finds all allocations for a specific project based on project id.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AllocationResponse.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<List<AllocationResponse>> getAllocationsByProject(@RequestParam(value = "project", required = true) String projectId) {
        List<AllocationResponse> allocation = allocationService.getAllocationByProjectId(projectId);
        if (allocation == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(allocation);
    }

    @GetMapping(EndPointConfig.ALLOCATIONS_BY_EMPLOYEE)
    @Transactional
    @Operation(
            summary = "Finds all allocations for a specific employee",
            description = "Finds all allocations for a specific employee based on employee id.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AllocationResponse.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<List<AllocationResponse>> getAllocationsByEmployee(@RequestParam(value = "employee", required = true) String employeeId) {
        List<AllocationResponse> allocation = allocationService.getAllocationByEmployeeId(employeeId);
        if (allocation == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(allocation);
    }
}
