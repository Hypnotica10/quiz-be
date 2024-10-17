package com.quiz.project.entity;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String username;

	private String password;

	private String email;

	@Column(updatable = false)
	private Instant createdDate;

	private Instant updateDate;

	private String avatar;

	@OneToMany(mappedBy = "user")
	private List<CourseEntity> listCourse;

	@PrePersist
	public void handleBeforeCreate() {
		this.createdDate = Instant.now();
		this.updateDate = Instant.now();
		this.avatar = "avatar-default.jpg";
	}

	@PreUpdate
	public void handleBeforeUpdate() {
		this.updateDate = Instant.now();
	}
}
