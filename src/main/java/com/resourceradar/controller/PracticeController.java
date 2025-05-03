package com.resourceradar.controller;

import java.util.List;

import com.resourceradar.dto.PracticeDTO;
import com.resourceradar.config.EndPointConfig;
import com.resourceradar.exception.PracticeNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.resourceradar.service.impl.PracticeServiceImpl;

@RestController
@RequestMapping(EndPointConfig.API_V1)
@Tag(name = "practices")
public class PracticeController {

	@Autowired
	private PracticeServiceImpl practiceService;

	@GetMapping(EndPointConfig.PRACTICE_DETAILS)
	public ResponseEntity<List<PracticeDTO>> getAllPractices() throws PracticeNotFoundException {
		List<PracticeDTO> practices = practiceService.getAllPractices();

		if (practices.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(practices);
		}
	}

	@GetMapping(EndPointConfig.PRACTICE_DETAILS_SEARCH)
	public ResponseEntity<List<PracticeDTO>> searchPracticeBasedOnName(@RequestParam("query") String query)
			throws PracticeNotFoundException {
//		List<Practice> skills = practiceService.getPracticeListBasedOnName(query);

//		if (skills.isEmpty()) {
//			return new ResponseEntity<List<PracticeDTO>>(HttpStatus.NO_CONTENT);
//		} else {
//			return new ResponseEntity<List<PracticeDTO>>(HttpStatus.OK);
//		}
        return null;
	}
	public ResponseEntity<Page<PracticeDTO>> getAllPractices (
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	) throws PracticeNotFoundException {
		Pageable pageable = PageRequest.of(page, size);
		Page<PracticeDTO> practices = practiceService.getAllPractices(pageable);

		if (practices.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(practices);
		}
	}

}
