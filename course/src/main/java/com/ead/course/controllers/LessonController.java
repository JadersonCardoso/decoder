package com.ead.course.controllers;

import com.ead.course.controllers.dtos.LessonDto;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class LessonController {

    private final LessonService lessonService;
    private final ModuleService moduleService;

    public LessonController(LessonService lessonService, ModuleService moduleService) {
        this.lessonService = lessonService;
        this.moduleService = moduleService;
    }
    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Object> saveLesson(@PathVariable UUID moduleId,
                                             @RequestBody @Valid LessonDto lessonDto) {
        Optional<ModuleModel> moduleModelOptional = this.moduleService.findById(moduleId);
        if (!moduleModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module Not Found.");
        }
        var lessonModel = new LessonModel();
        BeanUtils.copyProperties(lessonDto, lessonModel);
        lessonModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        lessonModel.setModule(moduleModelOptional.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(this.lessonService.save(lessonModel));
    }

    @DeleteMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> deleteLesson(@PathVariable UUID moduleId, @PathVariable UUID lessonId) {
        Optional<LessonModel> lessonModelOptional = this.lessonService.findLessonIntoModule(moduleId, lessonId);
        if (!lessonModelOptional.isPresent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");
        }
        this.lessonService.delete(lessonModelOptional.get());
        return ResponseEntity.ok("Lesson deleted successfully.");
    }
    @PutMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> updateLesson(@PathVariable UUID moduleId, @PathVariable UUID lessonId,
                                               @RequestBody @Valid LessonDto lessonDto) {
        Optional<LessonModel> lessonModelOptional = this.lessonService.findLessonIntoModule(moduleId, lessonId);
        if (!lessonModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");
        }
        lessonModelOptional.get().setTitle(lessonDto.getTitle());
        lessonModelOptional.get().setDescription(lessonDto.getDescription());
        lessonModelOptional.get().setVideoUrl(lessonDto.getVideoUrl());

        return ResponseEntity.ok(this.lessonService.save(lessonModelOptional.get()));

    }
    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Object> getAllLessons(@PathVariable UUID moduleId) {
        return ResponseEntity.ok(this.lessonService.findAllLessonsIntoModule(moduleId));
    }
    @GetMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> getOneLesson(@PathVariable UUID moduleId, @PathVariable UUID lessonId) {
        Optional<LessonModel> lessonModelOptional = this.lessonService.findLessonIntoModule(moduleId, lessonId);
        if (!lessonModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");
        }
        return ResponseEntity.ok(lessonModelOptional.get());
    }



}
