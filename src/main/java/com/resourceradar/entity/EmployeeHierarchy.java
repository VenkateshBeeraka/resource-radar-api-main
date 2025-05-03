package com.resourceradar.entity;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class EmployeeHierarchy {
	  
	  @Id
	  @GeneratedValue(generator = "uuid" , strategy = GenerationType.TABLE)
	  @GenericGenerator(name = "uuid", strategy = "uuid2")
	  private String id;
	  
	  private String name;
	  
	  private String designation;
	  
	  private Integer leftValue;
	  
	  private Integer rightValue;
	  @ManyToOne
	  private Employee employee;
	
}
