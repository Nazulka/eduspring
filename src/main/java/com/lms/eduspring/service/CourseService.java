package com.lms.eduspring.service;

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

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Section> getSectionsForCourse(Long id) {
        Course course = getCourseOrThrow(id);
        return course.getSections();
    }
}
