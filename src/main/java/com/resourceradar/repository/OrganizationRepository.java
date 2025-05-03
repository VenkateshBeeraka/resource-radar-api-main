package com.resourceradar.repository;

import com.resourceradar.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OrganizationRepository extends JpaRepository<Organization, String> {

    Optional<Organization> findById(String id);

}
