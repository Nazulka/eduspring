package com.lms.eduspring.repository;

import com.lms.eduspring.model.Course;
import com.lms.eduspring.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("select s from Section s where s.id = :sectionId")
    Optional<Section> findSectionById(@Param("sectionId") Long sectionId);
}
