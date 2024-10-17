package com.quiz.project.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "major")
@Setter
@Getter
public class MajorEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String description;

	@ManyToOne
	@JoinColumn(name = "subject_id", nullable = false)
	private SubjectEntity subject;

	@OneToMany(mappedBy = "major")
	private List<CourseEntity> listCourses;
}
