package com.resourceradar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.resourceradar.config.EndPointConfig;
import com.resourceradar.dto.DeleteStatus;
import com.resourceradar.dto.EmployeeDto;
import com.resourceradar.dto.EmployeesListReportingManager;
import com.resourceradar.dto.ReportingManagerDto;
import com.resourceradar.entity.Employee;
import com.resourceradar.exception.CustomValidationException;
import com.resourceradar.exception.DeleteResponse;
import com.resourceradar.exception.EmployeeNotFoundException;
import com.resourceradar.exception.OrgIdNotFoundException;
import com.resourceradar.exception.ResourceNotFoundException;
import com.resourceradar.repository.EmployeeRepository;
import com.resourceradar.service.impl.EmployeeServiceImpl;
import com.resourceradar.utils.Validator;

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

@RestController
@RequestMapping(EndPointConfig.API_V1 + EndPointConfig.EMPLOYEE)
@Tag(name = "Employee")
public class EmployeeController {

    @Autowired
    private EmployeeServiceImpl employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/create")
    @Operation(
            summary = "Create a new Employee",
            description = "Create a new Employee",
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
                                            array = @ArraySchema(schema = @Schema(implementation = EmployeeDto.class))
                                    )
                            }
                    ),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public EmployeeDto createEmployee(@RequestBody @Validated EmployeeDto employeeDTO, HttpServletRequest request)
            throws JsonProcessingException, OrgIdNotFoundException, ResourceNotFoundException {
        EmployeeDto emp = null;
        try {
            Validator.isValidate(employeeDTO);
            Employee employee = employeeRepository.findByOrgEmpIdOrEmail(employeeDTO.getOrgEmpId(), employeeDTO.getEmail());
            if (employee != null) {
                throw new ResourceNotFoundException("Employee  already exist with " + employeeDTO.getOrgEmpId() + " or email " + employeeDTO.getEmail());
            }
         
            if (employee == null) {
                emp = employeeService.createEmployee(employeeDTO, request);
            }
        } catch (CustomValidationException e) {
            throw new RuntimeException(e);
        }
        return emp;
    }

    @GetMapping()
    @Operation(
            summary = "Retrieve  list of all employees",
            description = "Retrieve  list of all employees.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = EmployeeDto.class))
                                    )
                            }
                    ),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        List<EmployeeDto> employees = employeeService.getAllEmployees();
        if (employees.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(employees);
    }

    @GetMapping(EndPointConfig.ID)
    @PreAuthorize("hasRole('ROLE_HR') OR hasRole('ROLE_Admin') OR hasRole('ROLE_ResourceManager') OR hasRole('ROLE_Manager')")
    @Transactional
    @Operation(
            summary = "Find an employee",
            description = "Finds an employee by employee Id.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable String id) throws EmployeeNotFoundException {
        EmployeeDto employee = employeeService.getEmployeebyId(id);
        if (employee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
        }
       return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @GetMapping(EndPointConfig.EMAIL_ID)
    @PreAuthorize("hasRole('ROLE_HR') OR hasRole('ROLE_Admin') OR hasRole('ROLE_ResourceManager') OR hasRole('ROLE_Manager')")
    @Transactional
    @Operation(
            summary = "Find an employee",
            description = "Finds an employee by email_id.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<Employee> getEmployeeByEmailID(@RequestParam("query") String query) throws ResourceNotFoundException {
        Employee employee = employeeService.getEmployeeByEmailId(query);
        if (employee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @GetMapping(EndPointConfig.EMPLOYEE_SEARCH_BY_NAME)
    @PreAuthorize("hasRole('ROLE_HR') OR hasRole('ROLE_Admin') OR hasRole('ROLE_ResourceManager') OR hasRole('ROLE_Manager')")
    @Transactional
    @Operation(
            summary = "Find an employee",
            description = "Finds an employee by firstname, lastname",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<List<EmployeeDto>> searchEmployee(@RequestParam String firstname, @RequestParam String lastname) throws EmployeeNotFoundException {
        List<EmployeeDto> employees = employeeService.searchEmployee(firstname, lastname);

        if (employees.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(employees, HttpStatus.OK);
        }
    }

    @DeleteMapping(EndPointConfig.ID)
    @PreAuthorize("hasRole('ROLE_HR') OR hasRole('ROLE_ResourceManager')")
    @Transactional
    @Operation(
            summary = "Deletes an employee",
            description = "Deletes an employee by Id.",
            responses = {
                    @ApiResponse(description = "Deleted", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public DeleteStatus deleteEmployee(@PathVariable("id") String id) throws EmployeeNotFoundException, DeleteResponse {
        DeleteStatus deleteEmployee = employeeService.deleteEmployee(id);
       return deleteEmployee;
    }

    @GetMapping(EndPointConfig.REPORTINGMANGER)
    @Operation(
            summary = "Retrieve  list of all reportingmanager",
            description = "Retrieves  list of all reportingmanagers.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = ReportingManagerDto.class))
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<List<ReportingManagerDto>> getReportingManager() {
        List<ReportingManagerDto> reportingManagers = employeeService.getReportingManagers();
      
            return  ResponseEntity.ok(reportingManagers);
        }
       
    @PutMapping(EndPointConfig.ID)
    @Transactional
    @PreAuthorize("hasRole('ROLE_HR') OR hasRole('ROLE_Admin') OR hasRole('ROLE_ResourceManager')")
    @Operation(
            summary = "Update details of a specific employee",
            description = "Updates employee information by passing in the employee Id and a JSON representation of the updated employee.",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER,
                             name = "X-Org-Id",
                             description = "Organization Id",
                             required = true,
                             schema = @Schema(type = "string"))},
            responses = {
                    @ApiResponse(
                            description = "Updated",
                            responseCode = "200",
                            links = @Link(name = "get", operationId = "get", parameters = @LinkParameter(name = "id", expression = "$request.body.id")),
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDto.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public DeleteStatus updateEmployee(@PathVariable String id, @RequestBody @Validated EmployeeDto employeeDTO, HttpServletRequest request) throws ResourceNotFoundException {
        DeleteStatus updatedEmployee = employeeService.updateEmployee(id, employeeDTO, request);
     
        
        return updatedEmployee;
    }  
    @GetMapping(EndPointConfig.SUBORDINATES)
    @Transactional
    @Operation(
            summary = "Find an employee subordinatess",
            description = "Finds an employee subordinates by empid.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDto.class))
      
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public List<EmployeeDto> getSubordinates(@PathVariable String empid) {
        return employeeService.getSubordinates(empid);
    }
    
    
    @PostMapping(EndPointConfig.REPORTINGMANGER)
    @Transactional
    public void setReportingManger(@RequestBody EmployeesListReportingManager emps) {
    	
    	List<EmployeeDto> empsDto = emps.getEmployees();
    	ReportingManagerDto reportingManager = emps.getReportingManager();
    	
    	
    	if (!empsDto.isEmpty() && reportingManager.getId() != null) {
    	
    	employeeService.setReportingManagers(empsDto, reportingManager.getId());
    	}
    }
}
