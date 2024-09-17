package com.ead.course.controllers;

import com.ead.course.clients.CourseClient;
import com.ead.course.controllers.dtos.SubscriptionDto;
import com.ead.course.controllers.dtos.UserDto;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.CourseUserService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseUserController {

    private final CourseClient courseClient;
    private final CourseService courseService;

    private final CourseUserService courseUserService;
    public CourseUserController(CourseClient courseClient, CourseService courseService, CourseUserService courseUserService) {
        this.courseClient = courseClient;
        this.courseService = courseService;
        this.courseUserService = courseUserService;
    }

    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<Page<UserDto>> getAllUsersByCourse(@PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable,
                                                             @PathVariable UUID courseId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.courseClient.getAllUsersByCourse(courseId, pageable));
    }
    @PostMapping("/courses/{courseId}/users/subscription")
    public ResponseEntity<Object> sabeSubscriptionUserInCourse(@PathVariable UUID courseId,
                                                               @RequestBody @Valid SubscriptionDto subscriptionDto) {
        Optional<CourseModel> courseModelOptional = this.courseService.findById(courseId);
        if (!courseModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not Found.");
        }
        if (this.courseUserService.existsByCourseAndUserId(courseModelOptional.get(), subscriptionDto.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erros: Subscription already exists!");
        }
        //TODO: verificação de user;
        var courseUserModel = this.courseUserService.save(courseModelOptional.get().convertToCourseUserModel(subscriptionDto.getUserId()));

        return ResponseEntity.status(HttpStatus.CREATED).body("Subscription created successfuççy.");
    }

}
