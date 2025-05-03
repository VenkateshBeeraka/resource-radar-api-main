package com.resourceradar.controller;

import java.util.List;

import com.resourceradar.config.EndPointConfig;
import com.resourceradar.entity.Skill;
import com.resourceradar.service.impl.SkillsServiceImpl;
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

import com.resourceradar.exception.SkillsNotFoundException;

@RestController
@RequestMapping(EndPointConfig.API_V1)
@Tag(name = "skills")
public class SkillsController {

	@Autowired
	private SkillsServiceImpl skillsService;

	@GetMapping(EndPointConfig.SKILLS)
	public ResponseEntity<List<Skill>> getAllSkills() throws SkillsNotFoundException {
		List<Skill> skills = skillsService.getAllSkills();

		if (skills.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(skills, HttpStatus.OK);
		}

	}
	 @GetMapping(EndPointConfig.SKILLS_DETAILS_SEARCH)
	    public ResponseEntity<List<Skill>> searchSkillsBasedOnName(@RequestParam("query") String query)  throws SkillsNotFoundException {
	        List<Skill> skills = skillsService.getSkillsListBasedOnName(query);

	        if (skills.isEmpty()) {
	            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	        } else {
	            return new ResponseEntity<>(skills, HttpStatus.OK);
	        }
	    }
	public ResponseEntity<Page<Skill>> getAllSkills(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	) throws SkillsNotFoundException {
		Pageable pageable = PageRequest.of(page, size);
		Page<Skill> skills = skillsService.getAllSkills(pageable);

		if (skills.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(skills, HttpStatus.OK);
		}
	}
	public ResponseEntity<Page<Skill>> searchSkillsBasedOnName(
			@RequestParam("query") String query,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	) throws SkillsNotFoundException {
		Pageable pageable = PageRequest.of(page, size);
		Page<Skill> skills = skillsService.getSkillsListBasedOnName(query, pageable);

		if (skills.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(skills, HttpStatus.OK);
		}
	}

}

