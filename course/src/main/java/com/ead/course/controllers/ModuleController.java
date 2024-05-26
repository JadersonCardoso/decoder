package com.ead.course.controllers;

import com.ead.course.controllers.dtos.ModuleDto;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ModuleController {

    private final ModuleService moduleService;
    private final CourseService courseService;

    public ModuleController(ModuleService moduleService, CourseService courseService) {
        this.moduleService = moduleService;
        this.courseService = courseService;
    }

    @PostMapping("/courses/{courseId}/modules")
    ResponseEntity<Object> saveModule(@PathVariable("courseId") UUID courseId,
                                      @RequestBody @Valid ModuleDto moduleDto) {
        Optional<CourseModel> courseModelOptional = this.courseService.findById(courseId);
        if (!courseModelOptional.isPresent()) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }
        var moduleModel = new ModuleModel();
        BeanUtils.copyProperties(moduleDto, moduleModel);
        moduleModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        moduleModel.setCourse(courseModelOptional.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(this.moduleService.save(moduleModel));
    }

    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteModule(@PathVariable("courseId") UUID courseId,
                                               @PathVariable("moduleId") UUID moduleId) {
        Optional<ModuleModel> moduleModelOptional = this.moduleService.findModuleIntoCourse(courseId, moduleId);
        if (!moduleModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course");
        }
        this.moduleService.delete(moduleModelOptional.get());
        return ResponseEntity.ok("Module deleted successfully.");
    }

    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteModule(@PathVariable("courseId") UUID courseId,
                                               @PathVariable("moduleId") UUID moduleId,
                                               @RequestBody @Valid ModuleDto moduleDto) {
        Optional<ModuleModel> moduleModelOptional = this.moduleService.findModuleIntoCourse(courseId, moduleId);
        if (!moduleModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course");
        }
        var moduleModel = moduleModelOptional.get();
        moduleModel.setTitle(moduleDto.getTitle());
        moduleModel.setDescription(moduleDto.getDescription());
        return ResponseEntity.ok(this.moduleService.save(moduleModel));
    }

    @GetMapping("/courses/{courseId}/modules")
    ResponseEntity<List<ModuleModel>> getAllModules(@PathVariable("courseId") UUID courseId) {
        return ResponseEntity.ok(this.moduleService.findAllByCourse(courseId));
    }

    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    ResponseEntity<Object> getOneModule(@PathVariable UUID courseId, @PathVariable("moduleId") UUID moduleId) {
        Optional<ModuleModel> moduleModelOptional = this.moduleService.findModuleIntoCourse(courseId, moduleId);
        if (!moduleModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course");
        }
        return ResponseEntity.ok(moduleModelOptional.get());
    }


}
