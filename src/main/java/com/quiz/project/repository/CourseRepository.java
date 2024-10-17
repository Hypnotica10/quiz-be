package com.quiz.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.quiz.project.entity.CourseEntity;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
	List<CourseEntity> findAllByUserId(Long id);

//	@Query(value = "SELECT * FROM course order by rand() limit 9", nativeQuery = true)
//	List<CourseEntity> findRandomNineCourse();

	Optional<CourseEntity> findAllByIdAndUserId(Long id, Long userId);

//	List<Staff> findByNameContainingOrAddressContainingOrStaffNoContaining(
//			  String name, String address, int staffNo, Pageable pageable);

	Page<CourseEntity> findByTitleContainingOrDescriptionContaining(String cname, String cdescription,
			Pageable pageable);

	Page<CourseEntity> findByMajorIdIn(List<Long> id, Pageable pageable);
}
