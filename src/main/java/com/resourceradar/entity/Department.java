package com.resourceradar.entity;

import org.hibernate.annotations.GenericGenerator;

import com.resourceradar.model.Auditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "departments")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Department extends Auditable {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "email_alias", nullable = false)
	private String email;

	@Column(name = "org_id")
	private String orgId;

	@Column(name = "type", nullable = false)
	private String type;
	
	
	@ManyToOne
	@JoinColumn(name = "parent_department_id")
	private Department parentDepartment;
}
