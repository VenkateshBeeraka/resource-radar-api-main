package com.resourceradar.repository;

import java.util.List;

import com.resourceradar.entity.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillsRepository extends JpaRepository<Skill, String> {

	List<Skill> findByNameContainingIgnoreCase(String name);

	Page<Skill> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
