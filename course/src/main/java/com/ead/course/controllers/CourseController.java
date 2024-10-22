package com.ead.course.controllers;

import com.ead.course.dtos.CourseDto;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.specifications.SpecificationTemplate;
import com.ead.course.validation.CourseValidator;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
@Log4j2
@RestController
@RequestMapping("/courses")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {

    private final CourseService courseService;
    private final CourseValidator courseValidator;

    public CourseController(CourseService courseService, CourseValidator courseValidator) {
        this.courseService = courseService;
        this.courseValidator = courseValidator;
    }

    @PostMapping
    ResponseEntity<Object> saveCourse(@RequestBody CourseDto courseDto, Errors errors) {
        log.debug("POST saveCourse courseDTO received {} ", courseDto.toString());
        this.courseValidator.validate(courseDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getAllErrors());
        }
        var courdeModel = new CourseModel();
        BeanUtils.copyProperties(courseDto, courdeModel);
        courdeModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        courdeModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        this.courseService.save(courdeModel);
        log.debug("POST saveCourse courseModel received {} ", courdeModel.toString());
        log.info("Course saved successfully courseId {} ", courdeModel.getCourseId());
        return ResponseEntity.status(HttpStatus.CREATED).body(courdeModel);
    }

    @DeleteMapping("/{courseId}")
    ResponseEntity<Object> deleteCourse(@PathVariable UUID courseId) {
        log.debug("DELETE deleteCourse courseId received {}", courseId);
        Optional<CourseModel> courseModelOptional = this.courseService.findById(courseId);
        if (!courseModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not Found");
        }
        this.courseService.delete(courseModelOptional.get());
        log.debug("DELETE deleteCourse courseId deleted {}", courseId);
        log.info("Course deleted sucsessfully courseId {}", courseId);
        return ResponseEntity.ok("Course deleted successfully.");
    }

    @PutMapping("/{courseId}")
    ResponseEntity<Object> updateCourse(@PathVariable UUID courseId, @RequestBody @Valid CourseDto courseDto) {
        Optional<CourseModel> courseModelOptional = this.courseService.findById(courseId);
        if (!courseModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not Found");
        }
        var courseModel = courseModelOptional.get();
        courseModel.setName(courseDto.getName());
        courseModel.setDescription(courseDto.getDescription());
        courseModel.setImageUrl(courseDto.getImageUrl());
        courseModel.setCourseStatus(courseDto.getCourseStatus());
        courseModel.setCourseLevel(courseDto.getCourseLevel());
        courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.OK).body(this.courseService.save(courseModel));
    }

    @GetMapping
    ResponseEntity<Page<CourseModel>> getAllCourses(SpecificationTemplate.CourseSpec spec,
                                                    @PageableDefault(page = 0, size = 10, sort = "courseId", direction = Sort.Direction.ASC)Pageable pageable,
                                                    @RequestParam(required = false) UUID userId) {
        if (userId != null) {
            return ResponseEntity.ok(this.courseService.findAll(SpecificationTemplate.courseUserId(userId).and(spec),pageable));
        }
        return ResponseEntity.ok(this.courseService.findAll(spec, pageable));
    }

    @GetMapping("/{courseId}")
    ResponseEntity<Object> getOneCourse(@PathVariable UUID courseId) {
        Optional<CourseModel> courseModelOptional = this.courseService.findById(courseId);
        if (!courseModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not Found");
        }
        return ResponseEntity.ok(this.courseService.findById(courseId).get());
    }
}
