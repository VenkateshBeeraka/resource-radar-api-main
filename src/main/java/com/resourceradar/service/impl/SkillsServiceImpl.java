package com.resourceradar.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.resourceradar.entity.Skill;
import com.resourceradar.exception.SkillsNotFoundException;
import com.resourceradar.repository.SkillsRepository;
import com.resourceradar.service.SkillsService;


@Service
public class SkillsServiceImpl implements SkillsService {

    @Autowired
    private SkillsRepository skillsRepository;

    @Override
    public List<Skill> getAllSkills() throws SkillsNotFoundException {

        try {
            List<Skill> skill = skillsRepository.findAll();

            return skill;
        } catch (Exception e) {
            throw new SkillsNotFoundException(" Skills Details not found");
        }
    }

    @Override
    public List<Skill> getSkillsListBasedOnName(String name) {

        List<Skill> skill = skillsRepository.findByNameContainingIgnoreCase(name);
        return skill;

    }
    @Override
    public Page<Skill> getAllSkills(Pageable pageable) throws SkillsNotFoundException {
        return skillsRepository.findAll(pageable);
    }


    @Override
    public Page<Skill> getSkillsListBasedOnName(String name, Pageable pageable) {
        return skillsRepository.findByNameContainingIgnoreCase(name, pageable);
    }
}
