package com.quiz.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.quiz.project.entity.SentenceEntity;

@Repository
public interface SentenceRepository extends JpaRepository<SentenceEntity, Long> {
	List<SentenceEntity> findAllByCourseId(Long id);

	@Query(value = "SELECT (Count(*)) FROM quizclone.sentence where course_id = ?1", nativeQuery = true)
	int countSentencesByCourseId(Long id);

//	@Query("delete from User where firstName = :firstName")
//	void deleteUsersByFirstName(@Param("firstName") String firstName);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM quizclone.sentence where course_id = ?1", nativeQuery = true)
	void deleteAllByCourseId(Long courseId);
	
	@Transactional
	void deleteByIdIn(List<Long> Ids);
}
