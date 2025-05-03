package com.resourceradar.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.resourceradar.dto.DepartmentDTO;
import com.resourceradar.dto.DesignationDTO;
import com.resourceradar.dto.OrganizationDTO;
import com.resourceradar.dto.PracticeDTO;
import com.resourceradar.dto.SkillDTO;
import com.resourceradar.entity.Department;
import com.resourceradar.entity.Designation;
import com.resourceradar.entity.Organization;
import com.resourceradar.entity.Practice;
import com.resourceradar.entity.Skill;
import com.resourceradar.repository.DepartmentRepository;
import com.resourceradar.repository.DesignationRepository;
import com.resourceradar.repository.OrganizationRepository;
import com.resourceradar.repository.PracticeRepository;
import com.resourceradar.repository.SkillsRepository;
import com.resourceradar.service.OrganizationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrganizationServiceImpl implements OrganizationService {

	@Autowired
	private OrganizationRepository organizationRepository;
	

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private DesignationRepository designationRepository;

	@Autowired
	private PracticeRepository practiceRepository;

	@Autowired
	private SkillsRepository skillsRepository;

	
	@Override
	public List<Organization> getAllOrganization() {
		return organizationRepository.findAll();
	}

	@Override
	public OrganizationDTO getOrgCompleteDetails(String request) {
		
		//String orgId = request.getHeader("orgId");
		OrganizationDTO org = new OrganizationDTO();
		Optional<Organization> orgOpt = organizationRepository.findById(request);
		if (orgOpt.isPresent()) {
			org.setId(orgOpt.get().getId());
			org.setName(orgOpt.get().getName());
		}

		List<Department> departments = departmentRepository.findAll();

		List<DepartmentDTO> departmentDTOs = new ArrayList<>();

		if (!departments.isEmpty()) {
			 log.info("checking is department empty ");

			for (Department department : departments) {

				DepartmentDTO departmentDTO = new DepartmentDTO();
				departmentDTO.setId(department.getId());
				departmentDTO.setName(department.getName());
				departmentDTO.setEmail(department.getEmail());
				departmentDTO.setType(department.getType());

				departmentDTOs.add(departmentDTO);
				
				
			}
		}
		org.setDepartments(departmentDTOs);
		
		List<Designation> designations = designationRepository.findAll();
		List<DesignationDTO> designationDTOs = new ArrayList<>();
		if (!designations.isEmpty()) {
			 log.info("checking is department empty ");
			for (Designation designation : designations) {
				DesignationDTO designationDTO = new DesignationDTO();
				designationDTO.setId(designation.getId());
				designationDTO.setName(designation.getName());
				designationDTOs.add(designationDTO);
			}
		}
		org.setDesignations(designationDTOs);

		List<Practice> practices = practiceRepository.findAll();
        List<PracticeDTO> practiceDTOs = new ArrayList<>();
        if (!practices.isEmpty()) {
            for (Practice practice : practices) {
                PracticeDTO practiceDTO = new PracticeDTO();
                practiceDTO.setOrgId(practice.getId());
                practiceDTO.setName(practice.getName());
                practiceDTO.setOrgId(practice.getOrgId());
                practiceDTO.setStartDate(practice.getCreatedDate());
                practiceDTO.setEndDate(practice.getModifiedDate());
                
                practiceDTOs.add(practiceDTO);
            }
        }
        org.setPractices(practiceDTOs);

        List<Skill> skills = skillsRepository.findAll();
        List<SkillDTO> skillsDTOs = new ArrayList<>();
        if (!skills.isEmpty()) {
             log.info("checking is Skills empty ");
            for (Skill skill : skills) {
                SkillDTO skillsDTO = new SkillDTO();
                skillsDTO.setId(skill.getId());
                skillsDTO.setName(skill.getName());
                skillsDTOs.add(skillsDTO);
            }
        }
        org.setSkills(skillsDTOs);
        return org;

 
    }

	}
		
		



