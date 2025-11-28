package com.lms.eduspring.controller;

import com.lms.eduspring.model.Course;
import com.lms.eduspring.model.Section;
import com.lms.eduspring.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

    @RestController
    @RequestMapping("/api/courses")
    public class CourseController {

        private final CourseService courseService;

        public CourseController(CourseService courseService) {
            this.courseService = courseService;
        }

        @GetMapping
        public List<Course> getAllCourses() {
            return courseService.getAllCourses();
        }


        @GetMapping("/{id}/sections")
        public List<Section> getCourseSections(@PathVariable Long id) {
            return courseService.getSectionsForCourse(id);
        }

    }



