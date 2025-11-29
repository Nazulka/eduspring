package com.lms.eduspring.service;

import com.lms.eduspring.dto.CourseDTO;
import com.lms.eduspring.dto.SectionDTO;
import com.lms.eduspring.exception.ResourceNotFoundException;
import com.lms.eduspring.model.Course;
import com.lms.eduspring.model.Section;
import com.lms.eduspring.repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course getCourseOrThrow(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    // ---- DTO versions ----

    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::mapCourseToDTO)
                .toList();
    }

    public List<SectionDTO> getSectionsForCourse(Long id) {
        Course course = getCourseOrThrow(id);
        return course.getSections()
                .stream()
                .map(this::mapSectionToDTO)
                .toList();
    }

    // ---- Mappers ----

    private CourseDTO mapCourseToDTO(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getTitle(),
                course.getDescription()
        );
    }

    private SectionDTO mapSectionToDTO(Section section) {
        return new SectionDTO(
                section.getId(),
                section.getContent()
        );
    }
}
