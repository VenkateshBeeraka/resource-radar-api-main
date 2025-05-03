package com.resourceradar.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.resourceradar.entity.Skill;
import com.resourceradar.exception.SkillsNotFoundException;

@Service
public interface SkillsService {

    public List<Skill> getAllSkills() throws SkillsNotFoundException;

    public List<Skill> getSkillsListBasedOnName(String name);


    public Page<Skill> getAllSkills(Pageable pageable) throws SkillsNotFoundException;

    public Page<Skill> getSkillsListBasedOnName(String name, Pageable pageable);

}
