package com.resourceradar.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.resourceradar.dto.ClientDTO;
import com.resourceradar.dto.ManagersDto;
import com.resourceradar.dto.ProjectDTO;
import com.resourceradar.dto.ProjectGetDTOResponse;
import com.resourceradar.entity.Allocation;
import com.resourceradar.entity.Client;
import com.resourceradar.entity.Employee;
import com.resourceradar.entity.Manager;
import com.resourceradar.entity.Organization;
import com.resourceradar.entity.Project;
import com.resourceradar.enums.ManagerType;
import com.resourceradar.enums.ProjectStatus;
import com.resourceradar.exception.OrgIdNotFoundException;
import com.resourceradar.exception.ProjectNotFoundException;
import com.resourceradar.exception.ResourceNotFoundException;
import com.resourceradar.repository.AllocationRepository;
import com.resourceradar.repository.ClientRepository;
import com.resourceradar.repository.EmployeeRepository;
import com.resourceradar.repository.ManagerRepository;
import com.resourceradar.repository.OrganizationRepository;
import com.resourceradar.repository.ProjectRepository;
import com.resourceradar.service.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private ManagerRepository managerRepository;

	@Autowired
	private AllocationRepository allocationRepository;
	
	@Autowired
	private EmployeeServiceImpl employeeServiceImpl;

	@Override
	public ProjectDTO createProject(ProjectDTO projectDTO, String orgId)
			throws OrgIdNotFoundException, ResourceNotFoundException {
		if (orgId == null) {
			throw new OrgIdNotFoundException("OrgId must not be null");
		}
		Project project = new Project();
		project.setName(projectDTO.getName());
		project.setType(projectDTO.getType());
		project.setStatus(projectDTO.getStatus());
		project.setOrgId(orgId);
		project.setStartDate(projectDTO.getStartDate());
		project.setEndDate(projectDTO.getEndDate());
		Optional<Client> client = clientRepository.findById(projectDTO.getClientId());
		project.setClient(client.get());
		if (projectDTO.getManagerId() == null) {
			throw new ResourceNotFoundException("Employee with Role of manager Id must not be null");
		}
		Optional<Employee> findById = employeeRepository.findById(projectDTO.getManagerId());
		Employee employee = findById.get();

		Manager manager = new Manager();
		manager.setName(employee.getFirstName() + " " + employee.getLastName());
		manager.setType(ManagerType.PROJECTMANAGER.toString());
		manager.setEmployeeId(employee.getOrgEmpId());
		manager.setClient(client.get());
		Optional<Organization> org = organizationRepository.findById(orgId);
		manager.setOrganization(org.get());

		managerRepository.save(manager);
		project.setManager(manager);
		Employee emp = employeeServiceImpl.idheader();
		if (emp != null) {
			project.setCreatedBy(emp.getFirstName() + "  " + emp.getLastName());
			project.setCreatedTime(LocalDateTime.now());
		}
		projectRepository.save(project);

		ProjectDTO dto = new ProjectDTO();
		dto.setName(project.getName());
		dto.setType(project.getType());
		dto.setStatus(project.getStatus());
		dto.setStartDate(project.getStartDate());
		dto.setEndDate(project.getEndDate());
		dto.setClientId(project.getClient().getId());
		dto.setManagerId(projectDTO.getManagerId());
		return dto;
	}

	@Override
	public ProjectDTO updateProject(String projectId, ProjectDTO projectDTO, String orgId)
			throws ProjectNotFoundException, OrgIdNotFoundException, ResourceNotFoundException {
		Optional<Project> pro = projectRepository.findById(projectId);
		if (pro == null) {
			throw new ProjectNotFoundException("Project not found with id " + projectId);
		}
		if (orgId == null) {
			throw new OrgIdNotFoundException("OrgId must not be null");
		}
		Project project = pro.get();
		if (projectDTO.getStatus().equalsIgnoreCase(ProjectStatus.COMPLETED.toString())) {

			List<Allocation> projectAllocations = allocationRepository.findByProjectId(projectId);
			if (!projectAllocations.isEmpty()) {
				for (Allocation allocation : projectAllocations) {
					if (allocation.isActive()) {
						allocation.setActive(false);
						allocation.setEndDate(LocalDateTime.now());
						allocationRepository.save(allocation);
					}
				}
			}
			project.setStatus(ProjectStatus.COMPLETED.toString());
		} else {
			project.setStatus(ProjectStatus.INPROGRESS.toString());
		}
		project.setName(projectDTO.getName());
		project.setType(projectDTO.getType());
		project.setStartDate(projectDTO.getStartDate());
		Optional<Client> client = clientRepository.findById(projectDTO.getClientId());
		project.setClient(client.get());
		if (projectDTO.getManagerId() == null) {
			throw new ResourceNotFoundException("Employee with Role of Manager Id must not be null");
		}

		Optional<Employee> findById = employeeRepository.findById(projectDTO.getManagerId());
		Employee employee = findById.get();

		Optional<Manager> findById2 = managerRepository.findById(project.getManager().getId());
		Manager manager = findById2.get();
		manager.setName(employee.getFirstName() + " " + employee.getLastName());
		manager.setType(ManagerType.PROJECTMANAGER.toString());
		manager.setEmployeeId(employee.getOrgEmpId());
		manager.setClient(client.get());
		Optional<Organization> org = organizationRepository.findById(orgId);
		manager.setOrganization(org.get());

		Manager save = managerRepository.save(manager);
		project.setManager(save);
		Employee emp = employeeServiceImpl.idheader();
		if (emp != null) {
			project.setModifiedBy(emp.getFirstName() + "  " + emp.getLastName());
			project.setModifiedTime(LocalDateTime.now());
		}
		projectRepository.save(project);

		ProjectDTO dto = new ProjectDTO();
		dto.setName(project.getName());
		dto.setType(project.getType());
		dto.setStatus(project.getStatus());
		dto.setStartDate(project.getStartDate());
		dto.setEndDate(project.getEndDate());
		dto.setClientId(project.getClient().getId());
		dto.setManagerId(projectDTO.getManagerId());
		return dto;
	}

	@Override
	public List<ProjectGetDTOResponse> getAllProjects() {
		List<Project> findAllProjects = projectRepository.findAll();
		List<ProjectGetDTOResponse> dtoResponses = new ArrayList<ProjectGetDTOResponse>();
		for (Project project : findAllProjects) {
//			if (!project.getStatus().equalsIgnoreCase(ProjectStatus.COMPLETED.toString())) {
			ProjectGetDTOResponse projectGetDTOResponse = new ProjectGetDTOResponse();
			projectGetDTOResponse.setId(project.getId());
			projectGetDTOResponse.setName(project.getName());
			projectGetDTOResponse.setType(project.getType());
			projectGetDTOResponse.setStatus(project.getStatus());
			projectGetDTOResponse.setStartDate(project.getStartDate());
			projectGetDTOResponse.setEndDate(project.getEndDate());

			ClientDTO clientDTO = new ClientDTO();
			clientDTO.setId(project.getClient().getId());
			clientDTO.setName(project.getClient().getName());
			clientDTO.setStatus(project.getClient().getStatus());
			clientDTO.setOrgId(project.getClient().getOrgId());
			clientDTO.setStartDate(project.getClient().getStartDate());
			clientDTO.setEndDate(project.getClient().getEndDate());
			List<Manager> man = managerRepository.findbyClientId(project.getClient().getId());
			for (Manager manager : man) {
				if (manager.getType().contentEquals(ManagerType.CLIENTMANAGER.toString())) {
					clientDTO.setClientManagerName(manager.getName());
				}
			}
			projectGetDTOResponse.setClient(clientDTO);
			projectGetDTOResponse.setOrgId(project.getOrgId());

			ManagersDto dto = new ManagersDto();
			dto.setId(project.getManager().getId());
			dto.setEmployeeId(project.getManager().getEmployeeId());
			dto.setName(project.getManager().getName());
			dto.setType(project.getManager().getType());
			projectGetDTOResponse.setManager(dto);
			dtoResponses.add(projectGetDTOResponse);
		}
		// }
		return dtoResponses;
	}

	@Override
	public ProjectGetDTOResponse getProjectById(String projectId) throws ProjectNotFoundException {
		Project project = projectRepository.findById(projectId).orElse(null);
//      if (!project.getStatus().equalsIgnoreCase(ProjectStatus.COMPLETED.toString())) {
		ProjectGetDTOResponse projectGetDTOResponse = new ProjectGetDTOResponse();
		projectGetDTOResponse.setId(project.getId());
		projectGetDTOResponse.setName(project.getName());
		projectGetDTOResponse.setType(project.getType());
		projectGetDTOResponse.setStatus(project.getStatus());
		projectGetDTOResponse.setStartDate(project.getStartDate());
		projectGetDTOResponse.setEndDate(project.getEndDate());

		ClientDTO clientDTO = new ClientDTO();
		clientDTO.setId(project.getClient().getId());
		clientDTO.setName(project.getClient().getName());
		clientDTO.setStatus(project.getClient().getStatus());
		clientDTO.setOrgId(project.getClient().getOrgId());
		clientDTO.setStartDate(project.getClient().getStartDate());
		clientDTO.setEndDate(project.getClient().getEndDate());
		List<Manager> man = managerRepository.findbyClientId(project.getClient().getId());
		for (Manager manager : man) {
			if (manager.getType().contentEquals(ManagerType.CLIENTMANAGER.toString())) {
				clientDTO.setClientManagerName(manager.getName());
			}
		}
		projectGetDTOResponse.setClient(clientDTO);
		projectGetDTOResponse.setOrgId(project.getOrgId());
		ManagersDto dto = new ManagersDto();
		dto.setId(project.getManager().getId());
		dto.setEmployeeId(project.getManager().getEmployeeId());
		dto.setName(project.getManager().getName());
		dto.setType(project.getManager().getType());
		projectGetDTOResponse.setManager(dto);
		return projectGetDTOResponse;
//     }
//     throw new ProjectNotFoundException( "Project is not present with this Id "+projectId);
	}

	@Override
	public String deleteProject(String projectId) throws ProjectNotFoundException {
		Project project = projectRepository.findById(projectId).orElse(null);
		if (project == null) {
			throw new ProjectNotFoundException("Project is not found with this id " + projectId);
		}
		List<Allocation> projectAllocations = allocationRepository.findByProjectId(projectId);
		if (!projectAllocations.isEmpty()) {
			for (Allocation allocation : projectAllocations) {
				if (allocation.isActive()) {
					allocation.setActive(false);
					allocation.setEndDate(LocalDateTime.now());
					allocationRepository.save(allocation);
				}
			}
		}
		project.setStatus(ProjectStatus.COMPLETED.toString());
		projectRepository.save(project);
		return "Project deleted successfully";
	}

	@Override
	public boolean isValidProject(String projectId) {
		// Check if the project ID is not null or empty
		if (projectId == null || projectId.isEmpty()) {
			return false;
		}

		// Check if the project ID exists in the database
		Optional<Project> project = projectRepository.findById(projectId);
		return project.isPresent();
	}
}
