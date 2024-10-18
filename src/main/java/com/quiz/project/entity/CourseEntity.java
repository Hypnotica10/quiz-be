package com.quiz.project.entity;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "course")
@Setter
@Getter
public class CourseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String description;

	@Column(updatable = false)
	private Instant createdDate;

	private Instant updatedDate;

	@ManyToOne
	@JoinColumn(name = "major_id", referencedColumnName = "id")
	private MajorEntity major;

	@OneToMany(mappedBy = "course")
	private List<SentenceEntity> listSentences;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@PrePersist
	public void handleBeforeCreate() {
		this.createdDate = Instant.now();
		this.updatedDate = Instant.now();
	}

	@PreUpdate
	public void handleBeforeUpdate() {
		this.updatedDate = Instant.now();
	}

	public String toString() {
		return this.id + " " + this.title + " " + this.description + " " + this.createdDate + " " + this.updatedDate ;
	}
}
