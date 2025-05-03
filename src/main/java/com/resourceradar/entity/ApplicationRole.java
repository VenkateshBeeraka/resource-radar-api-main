package com.resourceradar.entity;

import com.resourceradar.model.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "application_role")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRole extends Auditable {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "display_name", nullable = false)
	private String displayName;
}
