package com.resourceradar.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.resourceradar.dto.ClientRequest;
import com.resourceradar.dto.ClientResponse;
import com.resourceradar.entity.Allocation;
import com.resourceradar.entity.Client;
import com.resourceradar.entity.Employee;
import com.resourceradar.entity.Manager;
import com.resourceradar.entity.Organization;
import com.resourceradar.entity.Project;
import com.resourceradar.enums.ManagerType;
import com.resourceradar.enums.ProjectStatus;
import com.resourceradar.exception.ClientNotFoundException;
import com.resourceradar.exception.OrgIdNotFoundException;
import com.resourceradar.exception.ResourceNotFoundException;
import com.resourceradar.repository.AllocationRepository;
import com.resourceradar.repository.ClientRepository;
import com.resourceradar.repository.EmployeeRepository;
import com.resourceradar.repository.ManagerRepository;
import com.resourceradar.repository.OrganizationRepository;
import com.resourceradar.repository.ProjectRepository;
import com.resourceradar.service.ClientService;

@Service
public class ClientServiceImpl implements ClientService {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private ManagerRepository managerRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private AllocationRepository allocationRepository;
	
	@Autowired
	private EmployeeServiceImpl employeeServiceImpl;

	@Override
	public ClientRequest createClient(ClientRequest clientdto, String orgId) throws OrgIdNotFoundException {
		if (orgId == null) {
			throw new OrgIdNotFoundException("OrgId must not be null");
		}
		Client map = new Client();
		map.setName(clientdto.getName());
		map.setOrgId(orgId);
		map.setStatus(clientdto.getStatus());
		map.setStartDate(clientdto.getStartDate());
		map.setEndDate(clientdto.getEndDate());
		Employee emp = employeeServiceImpl.idheader();
		if (emp != null) {
			map.setCreatedBy(emp.getFirstName() + "  " + emp.getLastName());
			map.setCreatedTime(LocalDateTime.now());
		}
		clientRepository.save(map);

		Optional<Employee> findById = employeeRepository.findById(clientdto.getManagerId());
		Employee employee = findById.get();

		Manager manager = new Manager();
		manager.setName(employee.getFirstName() + " " + employee.getLastName());
		manager.setType(ManagerType.CLIENTMANAGER.toString());
		manager.setEmployeeId(employee.getOrgEmpId());
		manager.setClient(map);
		Optional<Organization> org = organizationRepository.findById(orgId);
		manager.setOrganization(org.get());

		managerRepository.save(manager);
		ClientRequest dto = new ClientRequest();
		dto.setName(map.getName());
		dto.setStatus(map.getStatus());
		dto.setStartDate(map.getStartDate());
		dto.setEndDate(map.getEndDate());
		dto.setManagerId(clientdto.getManagerId());
		return dto;
	}

	@Override
	public ClientRequest updateClientById(String clientid, ClientRequest client)
			throws ClientNotFoundException, ResourceNotFoundException {
		Client client2 = clientRepository.findById(clientid).orElse(null);
		if (client2 == null) {
			throw new ClientNotFoundException("Client is not found with this id " + clientid);
		}

		if (client.getStatus().equalsIgnoreCase(ProjectStatus.COMPLETED.toString())) {

			List<Project> listProjects = projectRepository.findByClientId(clientid);
			for (Project project : listProjects) {
				project.setStatus(ProjectStatus.COMPLETED.toString());
				projectRepository.save(project);
				List<Allocation> projectAllocations = allocationRepository.findByProjectId(project.getId());
				for (Allocation allocation : projectAllocations) {
					if (allocation.isActive()) {
						allocation.setActive(false);
						allocation.setEndDate(LocalDateTime.now());
						allocationRepository.save(allocation);
					}
				}
			}
			client2.setStatus(ProjectStatus.COMPLETED.toString());
		} else {
			client2.setStatus(ProjectStatus.INPROGRESS.toString());
		}
		
		List<Manager> man = managerRepository.findbyClientId(clientid);
		for (Manager manager : man) {
			if (manager.getType().contentEquals(ManagerType.CLIENTMANAGER.toString())) {
				if (client.getManagerId() == null) {
					throw new ResourceNotFoundException("Employee with Manger Role Id must not be null");
				}
				Optional<Employee> findById = employeeRepository.findById(client.getManagerId());
				Employee employee = findById.get();
				
				manager.setName(employee.getFirstName() + " " + employee.getLastName());
				manager.setType(ManagerType.CLIENTMANAGER.toString());
				manager.setEmployeeId(employee.getOrgEmpId());
				manager.setClient(client2);
				Optional<Organization> org = organizationRepository.findById(employee.getOrgId());
				manager.setOrganization(org.get());
				managerRepository.save(manager);
			}
		}

		client2.setName(client.getName());
		client2.setStartDate(client.getStartDate());
		client2.setEndDate(client.getEndDate());
		client2.setCreatedBy(client2.getCreatedBy());
		client2.setCreatedTime(client2.getCreatedTime());
		Employee emp = employeeServiceImpl.idheader();
		if (emp != null) {
			client2.setModifiedBy(emp.getFirstName() + "  " + emp.getLastName());
			client2.setModifiedTime(LocalDateTime.now());
		}
		clientRepository.save(client2);
		
		ClientRequest dto = new ClientRequest();
		dto.setName(client2.getName());
		dto.setStatus(client2.getStatus());
		dto.setStartDate(client2.getStartDate());
		dto.setEndDate(client2.getEndDate());
		dto.setManagerId(client.getManagerId());
		return dto;
	}

	@Override
	public ClientResponse getClientById(String id) throws ClientNotFoundException {
		Optional<Client> c = clientRepository.findById(id);
		Client client = c.get();
		if (client == null) {
			throw new ClientNotFoundException("Client is not found with this id " + id);
		}
		ClientResponse dto = new ClientResponse();
//	    if (!client.getStatus().equalsIgnoreCase(ClientStatus.COMPLETED.toString())) {
		dto.setId(client.getId());
		dto.setName(client.getName());
		dto.setStatus(client.getStatus());
		dto.setStartDate(client.getStartDate());
		dto.setEndDate(client.getEndDate());
		List<Manager> man = managerRepository.findbyClientId(client.getId());
		for (Manager manager : man) {
			if (manager.getType().contentEquals(ManagerType.CLIENTMANAGER.toString())) {
				dto.setManagerId(manager.getId());
				dto.setManagerName(manager.getName());
			}
		}
		return dto;
//      }
//      return dto;
	}

	@Override
	public List<ClientResponse> getAllClientsWithManager() {
		List<Client> findAll = clientRepository.findAll();
		List<ClientResponse> clientsDtos = new ArrayList<ClientResponse>();
		for (Client client : findAll) {
			// if (!client.getStatus().equalsIgnoreCase(ClientStatus.COMPLETED.toString())) {
			ClientResponse clientsDto = new ClientResponse();
			clientsDto.setId(client.getId());
			clientsDto.setName(client.getName());
			clientsDto.setStatus(client.getStatus());
			clientsDto.setStartDate(client.getStartDate());
			clientsDto.setEndDate(client.getEndDate());
			List<Manager> man = managerRepository.findbyClientId(client.getId());
			for (Manager manager : man) {
				if (manager.getType().contentEquals(ManagerType.CLIENTMANAGER.toString())) {
					clientsDto.setManagerId(manager.getId());
					clientsDto.setManagerName(manager.getName());
				}
			}
			clientsDtos.add(clientsDto);
		}
		// }
		return clientsDtos;
	}

	@Override
	public String deleteCLient(String clientid) throws ClientNotFoundException {
		Client client = clientRepository.findById(clientid).orElse(null);
		if (client == null) {
			throw new ClientNotFoundException("Client not found with this id ");
		}
		List<Project> listProjects = projectRepository.findByClientId(clientid);
		for (Project project : listProjects) {
			project.setStatus(ProjectStatus.COMPLETED.toString());
			projectRepository.save(project);
			List<Allocation> projectAllocations = allocationRepository.findByProjectId(project.getId());
			for (Allocation allocation : projectAllocations) {
				if (allocation.isActive()) {
					allocation.setActive(false);
					allocation.setEndDate(LocalDateTime.now());
					allocationRepository.save(allocation);
				}
			}
		}
		List<Manager> man = managerRepository.findbyClientId(clientid);
		for (Manager manager : man) {
			if (manager.getType().contentEquals(ManagerType.CLIENTMANAGER.toString())) {
				managerRepository.delete(manager);
			}
		}
		client.setStatus(ProjectStatus.COMPLETED.toString());
		clientRepository.save(client);
		return "Client deleted successfully";
	}
}
