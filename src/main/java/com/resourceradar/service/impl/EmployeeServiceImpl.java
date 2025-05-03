package com.resourceradar.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.resourceradar.dto.DeleteStatus;
import com.resourceradar.dto.EmployeeDto;
import com.resourceradar.dto.EmployeeOrgRolesDto;
import com.resourceradar.dto.EmployeeSkillsDto;
import com.resourceradar.dto.ReportingManagerDto;
import com.resourceradar.entity.ApplicationRole;
import com.resourceradar.entity.Department;
import com.resourceradar.entity.Employee;
import com.resourceradar.entity.EmployeeAudit;
import com.resourceradar.entity.EmployeeHierarchy;
import com.resourceradar.entity.EmployeeOrgRole;
import com.resourceradar.entity.EmployeeSkill;
import com.resourceradar.entity.Organization;
import com.resourceradar.entity.Skill;
import com.resourceradar.enums.EmployeeAuditEvent;
import com.resourceradar.exception.EmployeeNotFoundException;
import com.resourceradar.exception.OrgIdNotFoundException;
import com.resourceradar.exception.ResourceNotFoundException;
import com.resourceradar.repository.ApplicationRoleRepository;
import com.resourceradar.repository.DepartmentRepository;
import com.resourceradar.repository.EmployeeAuditRepository;
import com.resourceradar.repository.EmployeeHierarchyRepository;
import com.resourceradar.repository.EmployeeRepository;
import com.resourceradar.repository.OrganizationRepository;
import com.resourceradar.repository.SkillsRepository;
import com.resourceradar.service.EmployeeService;
import com.resourceradar.utils.Constants;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private SkillsRepository skillsRepository;

	@Autowired
	private ApplicationRoleRepository applicationRoleRepository;

	@Autowired
	private OrganizationRepository organizationRepository;
	@Autowired
	private EmployeeHierarchyRepository employeeHierarchyRepository;

	@Autowired
	private ModelMapper mapper;
	@Autowired
	private EmployeeAuditRepository employeeAuditRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	public Employee idheader() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Employee employee2 = null;
		if (authentication != null) {
			// Get the ID from the authentication object
			String id = authentication.getName();
			employee2 = employeeRepository.findByorgEmpId(id).orElse(null);
			log.info(id);
		}
		return employee2;
	}

	@Override
	public EmployeeDto createEmployee(EmployeeDto employeeDTO, HttpServletRequest request)
			throws OrgIdNotFoundException {

		String orgId = request.getHeader(Constants.ORG_ID);
		if (orgId == null) {
			throw new OrgIdNotFoundException("OrgId must not be null");
		}
		Optional<Organization> organization = organizationRepository.findById(orgId);
		Employee employee = mapper.map(employeeDTO, Employee.class);
		employee.setReportingManagerId(null);
		employee.setOrgId(orgId);
		Department dep = departmentRepository.findById(employeeDTO.getDepartment().getId()).orElse(null);
		employee.setDepartment(dep);

		if (!employeeDTO.getSkills().isEmpty()) {
			
			List<EmployeeSkillsDto> skills3 = employeeDTO.getSkills();
			boolean skillRepeated = false;
			Set<String> skillIds = new HashSet<>();

			for (EmployeeSkillsDto skill : skills3) {
			    if (skillIds.contains(skill.getId())) {
			        skillRepeated = true;
			        break;
			    }
			    skillIds.add(skill.getId());
			}

			if (skillRepeated) {
			    throw new OrgIdNotFoundException("Duplicate skill found in the employeeDTO");
			}

			
			
			
			
//			List<EmployeeSkillsDto> skills2 = employeeDTO.getSkills();
//
//			Set<EmployeeSkillsDto> uniqueSkills = new HashSet<>(skills2);
//			if (skills2.size() != uniqueSkills.size()) {
//			throw new OrgIdNotFoundException("duplicate skill found in employeeDto");
//			}

			
			Set<EmployeeSkill> employeeSkillsList = new HashSet<>();
			for (EmployeeSkillsDto employeeSkillDto : employeeDTO.getSkills()) {
				Optional<Skill> s = skillsRepository.findById(employeeSkillDto.getId());
				EmployeeSkill employeeSkill = new EmployeeSkill();
				employeeSkill.setSkill(s.get());
				employeeSkill.setEmployee(employee);
				employeeSkill.setIsPrimary(employeeSkillDto.getIsPrimary());
				employeeSkill.setName(employeeSkillDto.getName());
				employeeSkill.setOrganization(organization.get());
				employeeSkillsList.add(employeeSkill);
			}
			employee.setSkills(employeeSkillsList);
		}
		if (!employeeDTO.getRoles().isEmpty()) {
			Set<EmployeeOrgRole> employeeOrgRoles = new HashSet<>();
			for (EmployeeOrgRolesDto employeeOrgRoleDto : employeeDTO.getRoles()) {
				Optional<ApplicationRole> s = applicationRoleRepository.findById(employeeOrgRoleDto.getId());
				EmployeeOrgRole employeeOrgRole = new EmployeeOrgRole();
				employeeOrgRole.setApplicationRole(s.get());
				employeeOrgRole.setEmployee(employee);

				employeeOrgRole.setRole(s.get().getDisplayName());

				employeeOrgRole.setOrganization(organization.get());
				employeeOrgRoles.add(employeeOrgRole);
			}
			employee.setRoles(employeeOrgRoles);
		}

//		Employee id = idheader();
//		if (id != null) {
//			employee.setCreatedBy(id.getFirstName() + "  " + id.getLastName());
//			employee.setCreatedTime(LocalDateTime.now());
//		}

		Employee savedEmployee = employeeRepository.save(employee);
		String reportingManagerId = employeeDTO.getReportingManager().getId();
		if (savedEmployee != null) {
			createEmployeeReportingManager(reportingManagerId, savedEmployee);
		}
		log.info("employee save successfully =====>  " + savedEmployee.getId());
		createEmployeeAudit(savedEmployee, EmployeeAuditEvent.ADD, null, orgId);
		EmployeeDto employeeDto2 = mapper.map(savedEmployee, EmployeeDto.class);
		return employeeDto2;
	}

	public void createEmployeeReportingManager(String reportingMangerId, Employee savedEmployee) {
		if (reportingMangerId == null) {

			List<EmployeeHierarchy> findAll = employeeHierarchyRepository.findAll();
			if (findAll.isEmpty()) {
				EmployeeHierarchy emph = new EmployeeHierarchy();
				emph.setDesignation(savedEmployee.getDesignation());
				emph.setEmployee(savedEmployee);
				emph.setName(savedEmployee.getFirstName() + " " + savedEmployee.getLastName());
				emph.setLeftValue(1);
				emph.setRightValue(2);
				employeeHierarchyRepository.save(emph);
			}
		} else if (reportingMangerId != null) {
			List<EmployeeHierarchy> findAll = employeeHierarchyRepository.findAll();
			if (findAll.isEmpty()) {
				Employee emp = employeeRepository.findById(reportingMangerId).orElse(null);
				if (emp != null) {
					EmployeeHierarchy emph = new EmployeeHierarchy();
					emph.setDesignation(emp.getDesignation());
					emph.setEmployee(emp);
					emph.setName(emp.getFirstName() + " " + emp.getLastName());
					emph.setLeftValue(1);
					emph.setRightValue(2);
					employeeHierarchyRepository.save(emph);
				}
			}
		}
		if (reportingMangerId != null) {
			EmployeeHierarchy emph = new EmployeeHierarchy();
			EmployeeHierarchy employeeHierarchy = employeeHierarchyRepository.findByIdEmployeeId(reportingMangerId);
			if (employeeHierarchy != null) {
				savedEmployee.setReportingManagerId(reportingMangerId);
				employeeRepository.save(savedEmployee);
				EmployeeHierarchy manager = employeeHierarchyRepository.findByIdEmployeeId(reportingMangerId);
				List<EmployeeHierarchy> affectedNodes = employeeHierarchyRepository
						.findByRightValueGreaterThan(manager.getRightValue());
				int shift = 2;
				for (EmployeeHierarchy node : affectedNodes) {
					if (node.getLeftValue() > manager.getLeftValue()) {
						node.setLeftValue(node.getLeftValue() + shift);
					}
					node.setRightValue(node.getRightValue() + shift);
				}
				emph.setDesignation(savedEmployee.getDesignation());
				emph.setEmployee(savedEmployee);
				emph.setLeftValue(manager.getRightValue());
				emph.setRightValue(manager.getRightValue() + 1);
				emph.setName(savedEmployee.getFirstName() + " " + savedEmployee.getLastName());
				Object lock = new Object();
				synchronized (lock) {
					employeeHierarchyRepository.saveAll(affectedNodes);
					employeeHierarchyRepository.save(emph);
					manager.setRightValue(manager.getRightValue() + 2);
					employeeHierarchyRepository.save(manager);
				}
			}
		}
	}

	private void createEmployeeAudit(Employee newvalue, EmployeeAuditEvent add, Employee oldvalue, String orgId) {
		EmployeeAudit em = new EmployeeAudit();
		em.setEmployee(newvalue);
		em.setEventType(add.toString());
		em.setEventDate(LocalDateTime.now());
		em.setOrgId(orgId);
		if (add.toString().equalsIgnoreCase(EmployeeAuditEvent.ADD.toString())) {
			em.setNewValue("employee added with employeeId  " + newvalue.getId());
		}
		em.setNewValue("employee added");
		if (oldvalue != null && add.toString().equalsIgnoreCase(EmployeeAuditEvent.UPDATE.toString())) {
			em.setOldValue(oldvalue.getId());
		}
		employeeAuditRepository.save(em);
	}

	@Override
	public List<EmployeeDto> getAllEmployees() {

		List<Employee> employees = employeeRepository.findAll();
		List<EmployeeDto> employeesDtos = new ArrayList<>();

		for (Employee employee : employees) {
			Set<EmployeeSkill> skills = employee.getSkills();
			List<EmployeeSkillsDto> sl = new ArrayList<>();
			for (EmployeeSkill skill : skills) {
				String skillId = skill.getEmployeeSkills().getSkillId();
				EmployeeSkillsDto emps = new EmployeeSkillsDto();
				emps.setId(skillId);
				emps.setIsPrimary(skill.getIsPrimary());
				emps.setName(skill.getName());
				sl.add(emps);
			}
			Set<EmployeeOrgRole> roles = employee.getRoles();
			List<EmployeeOrgRolesDto> rl = new ArrayList<>();

			for (EmployeeOrgRole role : roles) {
				String roleid = role.getApplicationRole().getId();
				EmployeeOrgRolesDto rdto = new EmployeeOrgRolesDto();
				rdto.setId(roleid);
				rdto.setRole(role.getRole());
				rl.add(rdto);
			}
			ReportingManagerDto rm = new ReportingManagerDto();
			if (employee.getReportingManagerId() == null) {
				rm.setId(null);
				rm.setName(null);
			} else {
				Employee employee2 = employeeRepository.findById(employee.getReportingManagerId()).orElse(null);

				if (employee2 != null) {
					rm.setId(employee.getReportingManagerId());
					rm.setName(employee2.getFirstName() + " " + employee2.getLastName());
				}
			}
			log.info(employee.getReportingManagerId() + " mager id");
			EmployeeDto employeeDto = mapper.map(employee, EmployeeDto.class);
			employeeDto.setRoles(rl);
			employeeDto.setSkills(sl);
			employeeDto.setReportingManager(rm);
			employeesDtos.add(employeeDto);
		}
		return employeesDtos;
	}

	public EmployeeDto getEmployeebyId(String id) throws EmployeeNotFoundException {
		Employee emp = employeeRepository.findById(id).orElse(null);
		
			Set<EmployeeSkill> skills = emp.getSkills();

			List<EmployeeSkillsDto> sl = new ArrayList<>();
			for (EmployeeSkill skill : skills) {

				String skillId = skill.getEmployeeSkills().getSkillId();
				EmployeeSkillsDto emps = new EmployeeSkillsDto();
				emps.setId(skillId);
				emps.setIsPrimary(skill.getIsPrimary());
				emps.setName(skill.getName());
				sl.add(emps);
			}

			Set<EmployeeOrgRole> roles = emp.getRoles();
			List<EmployeeOrgRolesDto> rl = new ArrayList<>();

			for (EmployeeOrgRole role : roles) {
				String roleid = role.getApplicationRole().getId();
				EmployeeOrgRolesDto rdto = new EmployeeOrgRolesDto();

				rdto.setId(roleid);
				rdto.setRole(role.getRole());

				rl.add(rdto);
			}

			ReportingManagerDto rm = new ReportingManagerDto();

			if (emp.getReportingManagerId() == null) {
				rm.setId(null);
				rm.setName(null);
			} else {
				Employee employee2 = employeeRepository.findById(emp.getReportingManagerId()).orElse(null);
				if (employee2 != null) {
					rm.setId(emp.getReportingManagerId());
					rm.setName(employee2.getFirstName() + " " + employee2.getLastName());
				}
			}
			EmployeeDto employeeDtos = mapper.map(emp, EmployeeDto.class);
			employeeDtos.setRoles(rl);
			employeeDtos.setSkills(sl);
			employeeDtos.setReportingManager(rm);
			return employeeDtos;
		
			}

	@Override
	public List<EmployeeDto> searchEmployee(String firstname, String lastname) {
		List<Employee> employees = employeeRepository
				.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(firstname, lastname);
		List<EmployeeDto> employeesDtos = new ArrayList<>();

		for (Employee employee : employees) {

			Set<EmployeeSkill> skills = employee.getSkills();

			List<EmployeeSkillsDto> sl = new ArrayList<>();
			for (EmployeeSkill skill : skills) {

				String skillId = skill.getEmployeeSkills().getSkillId();
				EmployeeSkillsDto emps = new EmployeeSkillsDto();
				emps.setId(skillId);
				emps.setIsPrimary(skill.getIsPrimary());
				emps.setName(skill.getName());
				sl.add(emps);
			}

			Set<EmployeeOrgRole> roles = employee.getRoles();
			List<EmployeeOrgRolesDto> rl = new ArrayList<>();

			for (EmployeeOrgRole role : roles) {
				String roleid = role.getApplicationRole().getId();
				EmployeeOrgRolesDto rdto = new EmployeeOrgRolesDto();

				rdto.setId(roleid);
				rdto.setRole(role.getRole());

				rl.add(rdto);
			}

			ReportingManagerDto rm = new ReportingManagerDto();
			if (employee.getReportingManagerId() == null) {
				rm.setId(null);
				rm.setName(null);
			} else {
				Employee employee2 = employeeRepository.findById(employee.getReportingManagerId()).orElse(null);

				if (employee2 != null) {
					rm.setId(employee.getReportingManagerId());
					rm.setName(employee2.getFirstName() + " " + employee2.getLastName());
				}
			}

			log.info(employee.getReportingManagerId() + " mager id");

			EmployeeDto employeeDto = mapper.map(employee, EmployeeDto.class);
			employeeDto.setRoles(rl);
			employeeDto.setSkills(sl);
			employeeDto.setReportingManager(rm);
			employeesDtos.add(employeeDto);

		}
		return employeesDtos;
	}

	// update employee delete hierarchy of reporting employee
	public void deleteHierarchy(String id) {

		Employee employee = employeeRepository.findById(id).orElse(null);

		EmployeeHierarchy deletedManager = employeeHierarchyRepository.findByIdEmployeeId(id);

		if (deletedManager != null) {
			int deletedManagerLeftValue = deletedManager.getLeftValue();
			int deletedManagerRightValue = deletedManager.getRightValue();

			// Retrieve the affected hierarchies
			List<EmployeeHierarchy> affectedHierarchies = employeeHierarchyRepository
					.findByLeftValueBetween(deletedManagerLeftValue, deletedManagerRightValue);

			// Delete the affected hierarchies
			if (affectedHierarchies != null) {
				employeeHierarchyRepository.deleteAll(affectedHierarchies);
			}
			// Calculate the shift amount
			int shiftAmount = deletedManagerRightValue - deletedManagerLeftValue + 1;

			// Update the left and right values of the remaining hierarchies
			List<EmployeeHierarchy> remainingHierarchies = employeeHierarchyRepository.findAll();
			for (EmployeeHierarchy hierarchy : remainingHierarchies) {
				Employee employee2 = hierarchy.getEmployee();

				

				if (hierarchy.getLeftValue() > deletedManagerRightValue) {
					hierarchy.setLeftValue(hierarchy.getLeftValue() - shiftAmount);
					hierarchy.setRightValue(hierarchy.getRightValue() - shiftAmount);
				}

				if (hierarchy.getLeftValue() < deletedManagerRightValue
						&& hierarchy.getRightValue() > deletedManagerRightValue) {
					hierarchy.setRightValue(hierarchy.getRightValue() - shiftAmount);
				}
			}

			// Save the updated hierarchies back to the database
			employeeHierarchyRepository.saveAll(remainingHierarchies);
		}

		employee.setReportingManagerId(null);

		employeeRepository.save(employee);

	}

	@Override
	public DeleteStatus deleteEmployee(String id) {
		Employee employee = employeeRepository.findById(id).orElse(null);

		DeleteStatus dl = new DeleteStatus();
		List<EmployeeDto> subordinates = getSubordinates(employee.getId());

		if (!subordinates.isEmpty()) {

			dl.setSuboridinates(subordinates);
			dl.setMessage("Employee is reporting manager for these employees,First re-assign these employees");
			dl.setStatus("failed");

			return dl;

		} else {

			if (employee.isActive()) {

				log.info("status of employee  before deleting " + employee.isActive() + " employee id " + id);
				employee.setActive(false);
				employeeRepository.save(employee);
				log.info("status of employee  After deleting " + employee.isActive() + " employee id " + id);

			}

			dl.setSuboridinates(null);
			dl.setMessage("Employee Updated Successfully");
			dl.setStatus("Success");

			EmployeeHierarchy findByIdEmployeeId = employeeHierarchyRepository.findByIdEmployeeId(id);

			if (findByIdEmployeeId != null) {
				// Calculate the shift amount
				int shiftAmount = findByIdEmployeeId.getRightValue() - findByIdEmployeeId.getLeftValue() + 1;

				// Update the left and right values of the remaining hierarchies
				List<EmployeeHierarchy> remainingHierarchies = employeeHierarchyRepository.findAll();
				for (EmployeeHierarchy hierarchy : remainingHierarchies) {

					if (hierarchy.getLeftValue() > findByIdEmployeeId.getRightValue()) {
						hierarchy.setLeftValue(hierarchy.getLeftValue() - shiftAmount);
						hierarchy.setRightValue(hierarchy.getRightValue() - shiftAmount);
					}

					if (hierarchy.getLeftValue() < findByIdEmployeeId.getLeftValue()
							&& hierarchy.getRightValue() > findByIdEmployeeId.getRightValue()) {
						hierarchy.setRightValue(hierarchy.getRightValue() - shiftAmount);
					}
				}

				// Save the updated hierarchies back to the database
				employeeHierarchyRepository.saveAll(remainingHierarchies);
			}
			employeeHierarchyRepository.deleteByEmployeeId(id);
			createEmployeeAudit(employee, EmployeeAuditEvent.DELETE, null, null);
			return dl;

		}

	}

	@Override
	public Employee getEmployeeByEmailId(String emailId) throws ResourceNotFoundException {
		if (emailId == null) {
			throw new ResourceNotFoundException("EmailId must not be a null");
		}
		return employeeRepository.findByEmail(emailId);
	}

	@Override
	public List<ReportingManagerDto> getReportingManagers() {
		List<Employee> list = employeeRepository.findAll();

		List<ReportingManagerDto> list1 = new ArrayList<ReportingManagerDto>();

		if (!list.isEmpty()) {
			for (Employee employee : list) {
				if (employee.isActive()) {
					log.info(employee.getFirstName() + " " + employee.getLastName());
					Set<EmployeeOrgRole> roles = employee.getRoles();

					for (EmployeeOrgRole role : roles) {
						log.info(role.getRole());
						if (role.getRole().contains("Manager") || role.getRole().contains("ResourceManager")
								|| role.getRole().contains("HR") || role.getRole().contains("Admin")
								|| employee.getDesignation().equalsIgnoreCase("ceo")) {

							ReportingManagerDto md = new ReportingManagerDto();
							md.setId(employee.getId());
							md.setName(employee.getFirstName() + " " + employee.getLastName());

							list1.add(md);
						}
					}
				}
			}
		}
		return list1;
	}

	@Override
	public DeleteStatus updateEmployee(String empId, EmployeeDto employeeDTO, HttpServletRequest request) throws ResourceNotFoundException {

		String orgId = request.getHeader(Constants.ORG_ID);
		Organization organization = organizationRepository.findById(orgId).orElse(null);
		Optional<Employee> findById = employeeRepository.findById(empId);
		Employee employee = findById.get();
		employee.setOrgId(orgId);
		employee.setOrgEmpId(employeeDTO.getOrgEmpId());
		employee.setFirstName(employeeDTO.getFirstName());
		employee.setLastName(employeeDTO.getLastName());
		employee.setEmail(employeeDTO.getEmail());
		employee.setType(employeeDTO.getType());
		employee.setDesignation(employeeDTO.getDesignation());
		employee.setPractice(employeeDTO.getPractice());
		employee.setExpStartDate(employeeDTO.getExpStartDate());
		employee.setFissionStartDate(employeeDTO.getFissionStartDate());
		employee.setContactNumber(employeeDTO.getContactNumber());
		employee.setStatus(employeeDTO.getStatus());

		if (!employee.getDepartment().getId().equalsIgnoreCase(employeeDTO.getDepartment().getId())) {
			Optional<Department> department = departmentRepository.findById(employeeDTO.getDepartment().getId());
			Department dep = department.get();
			employee.setDepartment(dep);
		}

		employee.setGender(employeeDTO.getGender());
		employee.setLocation(employeeDTO.getLocation());
		employee.setNickname(employeeDTO.getNickname());
		employee.setNotes(employeeDTO.getNotes());
		employee.setBillable(employeeDTO.isBillable());

		if (!employeeDTO.getRoles().isEmpty()) {
			
			
			Set<EmployeeOrgRole> roles = employee.getRoles();
			Iterator<EmployeeOrgRole> iterator = roles.iterator();
			while (iterator.hasNext()) {
				EmployeeOrgRole employeeOrgRole = iterator.next();
				boolean roleExists = false;
				List<EmployeeOrgRolesDto> roles2 = employeeDTO.getRoles();
				for (EmployeeOrgRolesDto employeeOrgRole2 : roles2) {
					if (employeeOrgRole.getApplicationRole().getId().equalsIgnoreCase(employeeOrgRole2.getId())) {
						roleExists = true;
						break;
					}
				}
				if (!roleExists) {
					iterator.remove();
				}
			}

			for (EmployeeOrgRolesDto employeeOrgRole : employeeDTO.getRoles()) {
				boolean roleExists = false;
				for (EmployeeOrgRole existingRole : roles) {
					if (existingRole.getApplicationRole().getId().equalsIgnoreCase(employeeOrgRole.getId())) {
						roleExists = true;
						break;
					}
				}
				if (!roleExists) {
					Optional<ApplicationRole> role = applicationRoleRepository.findById(employeeOrgRole.getId());
					role.ifPresent(r -> {
						EmployeeOrgRole employeeOrg = new EmployeeOrgRole();
						employeeOrg.setEmployee(employee);
						employeeOrg.setApplicationRole(r);
						employeeOrg.setRole(r.getDisplayName());
						employeeOrg.setOrganization(organization);
						employee.getRoles().add(employeeOrg);
					});
				}
			}
		}

		if (!employeeDTO.getSkills().isEmpty()) {
			
			if (!employeeDTO.getSkills().isEmpty()) {
				
				List<EmployeeSkillsDto> skills3 = employeeDTO.getSkills();
				boolean skillRepeated = false;
				Set<String> skillIds = new HashSet<>();

				for (EmployeeSkillsDto skill : skills3) {
				    if (skillIds.contains(skill.getId())) {
				        skillRepeated = true;
				        break;
				    }
				    skillIds.add(skill.getId());
				}

				if (skillRepeated) {
				    throw new ResourceNotFoundException("Duplicate skill found in the employeeDTO");
				}
			
			}
			Set<EmployeeSkill> skills = employee.getSkills();
			
			Iterator<EmployeeSkill> iterator = skills.iterator();
			while (iterator.hasNext()) {
				EmployeeSkill employeeSkill = iterator.next();
				boolean skillExists = false;
			
				List<EmployeeSkillsDto> skills2 = employeeDTO.getSkills();
				
				for (EmployeeSkillsDto employeeSkill2 : skills2) {
					if (employeeSkill.getSkill().getId().equalsIgnoreCase(employeeSkill2.getId())) {
						skillExists = true;
						break;
					}
				}
				if (!skillExists) {
					iterator.remove();
				}
			}

			for (EmployeeSkillsDto employeeSkill : employeeDTO.getSkills()) {
				boolean skillExists = false;
				for (EmployeeSkill existingSkill : skills) {
					if (existingSkill.getSkill().getId().equalsIgnoreCase(employeeSkill.getId())) {
						skillExists = true;
						break;
					}
				}
				if (!skillExists) {
					Optional<Skill> skill = skillsRepository.findById(employeeSkill.getId());
					skill.ifPresent(s -> {
						EmployeeSkill employeeSk = new EmployeeSkill();
						employeeSk.setEmployee(employee);
						employeeSk.setSkill(s);
						employeeSk.setName(employeeSkill.getName());
						employeeSk.setIsPrimary(employeeSkill.getIsPrimary());
						employeeSk.setOrganization(organization);
						employee.getSkills().add(employeeSk);
					});
				}
			}
		}

		Employee employee2 = idheader();

		if (employee2 != null) {
			employee.setModifiedBy(employee2.getFirstName() + " " + employee2.getLastName());
			employee.setModifiedTime(LocalDateTime.now());
		}

		DeleteStatus dl = new DeleteStatus();

		if (!employeeDTO.isActive()) {

			DeleteStatus deleteStatus = deleteEmployee(empId);

			if (deleteStatus.getSuboridinates() == null) {
				employee.setActive(employeeDTO.isActive());
				employee.setReportingManagerId(null);
				Employee savedEmployee = employeeRepository.save(employee);
				EmployeeDto employeeDto2 = mapper.map(savedEmployee, EmployeeDto.class);

				dl.setSuboridinates(deleteStatus.getSuboridinates());
				dl.setStatus(deleteStatus.getStatus());
				dl.setMessage(deleteStatus.getMessage());
				dl.setEmployee(employeeDto2);

			} else {
				dl.setSuboridinates(deleteStatus.getSuboridinates());
				dl.setStatus(deleteStatus.getStatus());
				dl.setMessage(deleteStatus.getMessage());
				dl.setEmployee(null);

			}
		} else {

			Employee savedEmployee = employeeRepository.save(employee);
			EmployeeDto employeeDto2 = mapper.map(savedEmployee, EmployeeDto.class);
			dl.setEmployee(employeeDto2);
			dl.setMessage("Employee Updated Successfully");
			dl.setStatus("Success");
			dl.setSuboridinates(null);
			createEmployeeAudit(savedEmployee, EmployeeAuditEvent.UPDATE, employee, null);
			ReportingManagerDto reportingManager = employeeDTO.getReportingManager();
			if (reportingManager.getId() != null && employee.getReportingManagerId() != reportingManager.getId()) {
				if (employee.getReportingManagerId() != null) {
					deleteHierarchy(employee.getId());
				}
				
				log.info("updating reporting manager");
				createEmployeeReportingManager(reportingManager.getId(), savedEmployee);
				employee.setReportingManagerId(reportingManager.getId());
				employeeRepository.save(employee);
				log.info("employee save successfully =====>  " + savedEmployee.getId());
			
			}
		}
		return dl;

	}

	@Override
	public List<EmployeeDto> getSubordinates(String empid) {
		EmployeeHierarchy manager = employeeHierarchyRepository.findByIdEmployeeId(empid);

		List<EmployeeDto> emps = new ArrayList<>();
		if (manager != null) {
			log.info("employee id : " + empid + " name " + manager.getName());
			List<EmployeeHierarchy> affectedEmployees = employeeHierarchyRepository
					.findByLeftValueGreaterThanAndRightValueLessThan(manager.getLeftValue(), manager.getRightValue());
			for (EmployeeHierarchy employeeHierarchy : affectedEmployees) {
				EmployeeDto employeeDto = mapper.map(employeeHierarchy.getEmployee(), EmployeeDto.class);
				emps.add(employeeDto);
			}
		}
		return emps;

	}

	@Override
	public boolean isValidEmployee(String employeeId) {
		// Check if the employee ID is not null or empty
		if (employeeId == null || employeeId.isEmpty()) {
			return false;
		}

		// Check if the employee ID exists in the database
		Optional<Employee> employee = employeeRepository.findById(employeeId);
		return employee.map(Employee::isActive).orElse(false);

	}

	@Override
	public void setReportingManagers(List<EmployeeDto> emps, String empId) {

		List<Employee> list = emps.stream().map(p -> mapper.map(p, Employee.class)).collect(Collectors.toList());

		for (Employee employee : list) {

			employeeHierarchyRepository.deleteByEmployeeId(employee.getId());

			EmployeeHierarchy emph = new EmployeeHierarchy();

			EmployeeHierarchy employeeHierarchy = employeeHierarchyRepository.findByIdEmployeeId(empId);
			if (employeeHierarchy != null) {

				List<EmployeeHierarchy> affectedNodes = employeeHierarchyRepository
						.findByRightValueGreaterThan(employeeHierarchy.getRightValue());

				int shift = 2;
				for (EmployeeHierarchy node : affectedNodes) {

					if (node.getLeftValue() > employeeHierarchy.getLeftValue()) {
						node.setLeftValue(node.getLeftValue() + shift);
					}
					node.setRightValue(node.getRightValue() + shift);
				}

				emph.setDesignation(employee.getDesignation());
				emph.setEmployee(employee);
				emph.setLeftValue(employeeHierarchy.getRightValue());
				emph.setRightValue(employeeHierarchy.getRightValue() + 1);
				emph.setName(employee.getFirstName() + " " + employee.getLastName());

				employeeHierarchyRepository.saveAll(affectedNodes);
				employeeHierarchyRepository.save(emph);
				employeeHierarchy.setRightValue(employeeHierarchy.getRightValue() + 2);
				employeeHierarchyRepository.save(employeeHierarchy);
			}
		}
	}

}
