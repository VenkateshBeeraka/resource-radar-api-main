package com.resourceradar.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.resourceradar.dto.AllocationRequest;
import com.resourceradar.dto.AllocationResponse;
import com.resourceradar.entity.Allocation;
import com.resourceradar.entity.AllocationAudit;
import com.resourceradar.entity.Client;
import com.resourceradar.entity.Employee;
import com.resourceradar.entity.Organization;
import com.resourceradar.entity.Project;
import com.resourceradar.exception.EmployeeAllocationNotFoundException;
import com.resourceradar.model.AuditEventType;
import com.resourceradar.repository.AllocationAuditRepository;
import com.resourceradar.repository.AllocationRepository;
import com.resourceradar.repository.EmployeeRepository;
import com.resourceradar.repository.OrganizationRepository;
import com.resourceradar.repository.ProjectRepository;
import com.resourceradar.service.AllocationService;

@Service
public class AllocationServiceImpl implements AllocationService {

	@Autowired
	private AllocationRepository allocationRepository;
	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private AllocationAuditRepository allocationAuditRepository;

	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private EmployeeServiceImpl employeeServiceImpl;

	@Override
	public AllocationResponse createAllocation(AllocationRequest allocationRequest, String orgId) throws EmployeeAllocationNotFoundException {
//    	AllocationMapper.INSTANCE.mapToAllocation(allocationRequest);
		Allocation allocation = new Allocation();
		Optional<Organization> organization = organizationRepository.findById(orgId);
		Optional<Employee> employee = employeeRepository.findById(allocationRequest.getEmployeeId());
		Optional<Project> project = projectRepository.findById(allocationRequest.getProjectId());
		Client client = null;
		if (project.isPresent()) {
			client = project.get().getClient();
		}

		allocation.setEmployee(employee.get());
		allocation.setProject(project.get());
		allocation.setClient(client);
		allocation.setOrganization(organization.get());
		allocation.setRole(allocationRequest.getRole());
		allocation.setJobType(allocationRequest.getJobType());
		allocation.setBillable(allocationRequest.isBillable());
		allocation.setUtilization(allocationRequest.getUtilization());
		allocation.setNotes(allocationRequest.getNotes());
		allocation.setStartDate(allocationRequest.getStartDate());
		if (allocationRequest.isActive()) {
			allocation.setActive(allocationRequest.isActive());
			allocation.setEndDate(null);
		} else {
			allocation.setActive(false);
			allocation.setEndDate(LocalDateTime.now());
		}
		
		if (allocationRequest.isPrimary()) {
			List<AllocationResponse> allocationByEmployeeId = getAllocationByEmployeeId(employee.get().getId());
			for (AllocationResponse allocationResponse : allocationByEmployeeId) {
				if (allocationResponse.isPrimary()) {
					throw new EmployeeAllocationNotFoundException("Employee Already have a Primary project Allocation with this ID = " + allocationResponse.getId());
				}
			}
		}
		allocation.setPrimary(allocationRequest.isPrimary());
		Employee emp = employeeServiceImpl.idheader();
		if (emp != null) {
			allocation.setCreatedBy(emp.getFirstName() + "  " + emp.getLastName());
			allocation.setCreatedTime(LocalDateTime.now());
		}

		Allocation savedAllocation = allocationRepository.save(allocation);

		createAllocationAuditLog(savedAllocation, AuditEventType.CREATE.toString());
//      AllocationMapper.INSTANCE.mapToAllocationResponse(savedAllocation);
		return mapper.map(savedAllocation, AllocationResponse.class); // TODO: Handle other failures
	}

	@Override
	public AllocationResponse updateAllocation(String id, AllocationRequest allocationRequest)
			throws EmployeeAllocationNotFoundException {
		Allocation existingAllocation = allocationRepository.findById(id).orElse(null);

		if (existingAllocation == null) {
			throw new EmployeeAllocationNotFoundException("Employee allocation not found with id " + id);
		}

//      AllocationMapper.INSTANCE.mapToAllocation(allocationRequest);
//      Allocation allocation = mapper.map(allocationRequest, Allocation.class);
		Allocation allocation = new Allocation();
		Optional<Employee> employee = employeeRepository.findById(allocationRequest.getEmployeeId());
		Optional<Project> project = projectRepository.findById(allocationRequest.getProjectId());
		Client client = null;
		if (project.isPresent()) {
			client = project.get().getClient();
		}

		allocation.setId(id);
		allocation.setEmployee(employee.get());
		allocation.setProject(project.get());
		allocation.setClient(client);
		allocation.setRole(allocationRequest.getRole());
		allocation.setJobType(allocationRequest.getJobType());
		allocation.setBillable(allocationRequest.isBillable());
		allocation.setUtilization(allocationRequest.getUtilization());
		allocation.setNotes(allocationRequest.getNotes());
		allocation.setStartDate(allocationRequest.getStartDate());
		if (allocationRequest.isActive()) {
			if (allocation.getEndDate() != null) {
				allocation.setActive(allocationRequest.isActive());
				allocation.setEndDate(null);

			} else {
				throw new EmployeeAllocationNotFoundException("Cannot reactivate the allocation");
			}

		} else if (allocationRequest.getEndDate() != null) {
			if (allocation.getEndDate() == null) {
				allocation.setActive(false);
				allocation.setEndDate(allocationRequest.getEndDate());
			}
		}
		
		if (allocationRequest.isPrimary()) {
			List<AllocationResponse> allocationByEmployeeId = getAllocationByEmployeeId(employee.get().getId());
			for (AllocationResponse allocationResponse : allocationByEmployeeId) {
				if (allocationResponse.isPrimary()) {
					throw new EmployeeAllocationNotFoundException("Employee Already have a Primary project Allocation with this ID = " + allocationResponse.getId());
				}
			}
		}
		allocation.setPrimary(allocationRequest.isPrimary());
		allocation.setCreatedBy(existingAllocation.getCreatedBy());
		allocation.setCreatedTime(existingAllocation.getCreatedTime());
		Employee emp = employeeServiceImpl.idheader();
		if (emp != null) {
			allocation.setModifiedBy(emp.getFirstName() + "  " + emp.getLastName());
			allocation.setModifiedTime(LocalDateTime.now());
		}

		Allocation savedAllocation = allocationRepository.save(allocation);

		createAuditLog(savedAllocation, AuditEventType.UPDATE.toString(), existingAllocation, savedAllocation);
//      AllocationMapper.INSTANCE.mapToAllocationResponse(savedAllocation);
		return mapper.map(savedAllocation, AllocationResponse.class);
	}

	@Override
	public List<AllocationResponse> getAllAllocations() {
//    	AllocationMapper.INSTANCE.map(allocationRepository.findAll());
		List<AllocationResponse> listAllocation = new ArrayList<AllocationResponse>();
		for (Allocation allocation : allocationRepository.findAll()) {
			AllocationResponse allow = mapper.map(allocation, AllocationResponse.class);
			allow.setProjectName(allocation.getProject().getName());
			listAllocation.add(allow);
		}
		return listAllocation;
	}

	@Override
	public AllocationResponse getAllocationById(String id) {
//      optionalAllocation.map(AllocationMapper.INSTANCE::mapToAllocationResponse).orElse(null);
		Optional<Allocation> optionalAllocation = allocationRepository.findById(id);
		AllocationResponse entityTOdto = mapper.map(optionalAllocation.get(), AllocationResponse.class);
		entityTOdto.setProjectName(optionalAllocation.get().getProject().getName());
		return entityTOdto;
	}

	@Override
	public List<AllocationResponse> getAllocationByEmployeeId(String employeeId) {
//    	AllocationMapper.INSTANCE.map(allocationRepository.findByEmployeeId(employeeId));
		List<AllocationResponse> listResponses = new ArrayList<AllocationResponse>();
		List<Allocation> listEMployeeAllocation = allocationRepository.findByEmployeeId(employeeId);
		for (Allocation allocation : listEMployeeAllocation) {
			AllocationResponse allocationResponse = mapper.map(allocation, AllocationResponse.class);
			allocationResponse.setProjectName(allocation.getProject().getName());
			listResponses.add(allocationResponse);
		}
		return listResponses;
	}

	@Override
	public List<AllocationResponse> getAllocationByProjectId(String projectId) {
//    	AllocationMapper.INSTANCE.map(allocationRepository.findByProjectId(projectId));
		List<AllocationResponse> listResponses = new ArrayList<AllocationResponse>();
		List<Allocation> projectAllocations = allocationRepository.findByProjectId(projectId);
		for (Allocation allocation : projectAllocations) {
			AllocationResponse allocationResponse = mapper.map(allocation, AllocationResponse.class);
			allocationResponse.setProjectName(allocation.getProject().getName());
			listResponses.add(allocationResponse);
		}
		return listResponses;
	}

	@Override
	public String deleteEmployeeAllocation(String id) throws EmployeeAllocationNotFoundException {
		Allocation deletedAllocation = allocationRepository.findById(id).orElse(null);
		if (deletedAllocation == null) {
			throw new EmployeeAllocationNotFoundException("Employee allocation not found with id " + id);
		}

		// TODO: Handle updating status depending on Active or inActive
		allocationRepository.deleteById(id);

		createAuditLog(deletedAllocation, AuditEventType.DELETE.toString(), deletedAllocation, null);

		return "Allocation Deleted successfully";
	}

	@Override
	public boolean isDuplicateAllocation(String employeeId, String projectId) {
		Allocation findAllocationByEmployeeIdAndProjectId = allocationRepository.findAllocationByEmployeeIdAndProjectId(employeeId, projectId);
		if (findAllocationByEmployeeIdAndProjectId != null) {
			if (findAllocationByEmployeeIdAndProjectId.isActive()) {
				return true;
			}
		}
		return false;
	}

	private void createAllocationAuditLog(Allocation allocation, String eventType) {
		AllocationAudit auditLog = new AllocationAudit();
		auditLog.setAllocationId(allocation.getId());
		auditLog.setEventType(eventType.toString());
		auditLog.setEventDate(LocalDateTime.now());
		auditLog.setNewValue(allocation.toString());
		allocationAuditRepository.save(auditLog);
	}

	private void createAuditLog(Allocation allocation, String eventType, Allocation oldAllocation, Allocation newAllocation) {
		AllocationAudit auditLog = new AllocationAudit();
		auditLog.setAllocationId(allocation.getId());
		auditLog.setEventType(eventType);
		auditLog.setEventDate(LocalDateTime.now());
		auditLog.setOldValue(oldAllocation.toString());
		if (newAllocation != null) {
			auditLog.setNewValue(newAllocation.toString());
		}
		auditLog.setEndDate(oldAllocation.getStartDate());

		allocationAuditRepository.save(auditLog);
	}
}
