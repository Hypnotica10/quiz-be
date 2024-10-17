package com.quiz.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quiz.project.entity.MajorEntity;

@Repository
public interface MajorRepository extends JpaRepository<MajorEntity, Long> {
	boolean existsByName(String name);

	List<MajorEntity> findAllBySubjectId(Long id);
}
