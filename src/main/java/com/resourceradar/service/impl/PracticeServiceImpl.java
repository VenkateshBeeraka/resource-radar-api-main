package com.resourceradar.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.resourceradar.dto.PracticeDTO;
import com.resourceradar.entity.Practice;
import com.resourceradar.exception.PracticeNotFoundException;
import com.resourceradar.repository.PracticeRepository;
import com.resourceradar.service.PracticeService;

@Service
public class PracticeServiceImpl implements PracticeService {

	@Autowired
	private PracticeRepository practiceRepository;

	@Override
	public List<PracticeDTO> getAllPractices() throws PracticeNotFoundException {

		try {
			List<Practice> practices = practiceRepository.findAll();
//          return practices.stream().map(this::convertToDto).collect(Collectors.toList());
			List<PracticeDTO> practiceDTOs = new ArrayList<>();
			for (Practice p : practices) {
				PracticeDTO pDto = new PracticeDTO();
				pDto.setOrgId(p.getId());
				pDto.setName(p.getName());
				pDto.setOrgId(p.getOrgId());
				pDto.setStartDate(p.getCreatedDate());
				pDto.setEndDate(p.getModifiedDate());
				practiceDTOs.add(pDto);
			}
			return practiceDTOs;
		} catch (Exception e) {
			throw new PracticeNotFoundException("Practice Details not found" + e);
		}
	}

	@Override
	public List<PracticeDTO> getPracticeListBasedOnName(String name) {

//		List<Practice> practices = practiceRepository.findByNameContainingIgnoreCase(name);
//		return (PracticeDTO) practices.stream().map(this::convertToDto).collect(Collectors.toList());
		return null;

	}

	@Override
	public Page<PracticeDTO> getAllPractices(Pageable pageable) throws PracticeNotFoundException {
		Page<Practice> practicePage = practiceRepository.findAll(pageable);
		List<PracticeDTO> practiceDTOs = practicePage.getContent().stream().map(this::convertToDto)
				.collect(Collectors.toList());

		return new PageImpl<>(practiceDTOs, pageable, practicePage.getTotalElements());
	}

	@Override
	public Page<PracticeDTO> getPracticeListBasedOnName(String name, Pageable pageable) {
		Page<Practice> practicePage = practiceRepository.findByNameContainingIgnoreCase(name, pageable);
		List<PracticeDTO> practiceDTOs = practicePage.getContent().stream().map(this::convertToDto)
				.collect(Collectors.toList());

		return new PageImpl<>(practiceDTOs, pageable, practicePage.getTotalElements());
	}

	@Override
	public PracticeDTO convertToDto(Practice practice) {
		PracticeDTO practiceDto = new PracticeDTO();
		practiceDto.setOrgId(practice.getId());
		practiceDto.setName(practice.getName());
		return practiceDto;
	}

//	@Override
//	public PracticeDTO convertToDTo(Practice practice) {
//		PracticeDTO practiceDto = new PracticeDTO();
//		practiceDto.setId(practice.getId());
//		practiceDto.setName(practice.getName());
//		// Set other properties as needed
//		return practiceDto;
//	}

	/*
	 * private PracticeDTO convertToDto(Practice practice) { PracticeDTO practiceDto
	 * = new PracticeDTO(); //practiceDto.setId(practice.getId());
	 * practiceDto.setName(practice.getName());
	 * 
	 * // LocalDateTime createdDateTime =
	 * practice.getCreatedDateTime().atZone(ZoneId.systemDefault()).toLocalDateTime(
	 * ); // LocalDateTime modifiedDateTime =
	 * practice.getModifiedDateTime().atZone(ZoneId.systemDefault()) //
	 * .toLocalDateTime();
	 * 
	 * practiceDto.setCreatedDateTime(practice.getCreatedDate());
	 * practiceDto.setModifiedDateTime(practice.getModifiedDate());
	 * 
	 * return practiceDto; }
	 * 
	 * private Practice convertToEntity(PracticeDTO practiceDto) { Practice practice
	 * = new Practice(); practice.setId(practiceDto.getId());
	 * practice.setName(practiceDto.getName());
	 * 
	 * Instant createdDateTime =
	 * practiceDto.getCreatedDateTime().atZone(ZoneId.systemDefault()).toInstant();
	 * Instant modifiedDateTime =
	 * practiceDto.getModifiedDateTime().atZone(ZoneId.systemDefault()).toInstant();
	 * 
	 * practice.setCreatedDateTime(createdDateTime);
	 * practice.setModifiedDateTime(modifiedDateTime);
	 * 
	 * return practice; }
	 */

	private Practice createEntityFromDto(PracticeDTO practiceDto) {
		Practice practice = new Practice();
		practice.setName(practiceDto.getName());
		practice.setCreatedDateTime(Instant.now());
		practice.setModifiedDateTime(Instant.now());

		return practice;
	}
}
