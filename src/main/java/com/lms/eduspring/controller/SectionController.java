package com.lms.eduspring.controller;

import com.lms.eduspring.dto.SectionDTO;
import com.lms.eduspring.service.CourseService;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sections")
public class SectionController {

    private final CourseService courseService;

    public SectionController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/{id}")
    @PermitAll // dev-friendly
    public SectionDTO getSection(@PathVariable Long id) {
        return courseService.getSectionById(id);
    }
}
