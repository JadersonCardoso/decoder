package com.ead.authuser.controllers;

import com.ead.authuser.clients.CourseClient;
import com.ead.authuser.dtos.CourseDto;
import com.ead.authuser.dtos.UserCourseDTO;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserCourseService;
import com.ead.authuser.services.UserService;
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
public class UserCourseController {
    private final CourseClient userClient;
    private final UserService userService;
    private final UserCourseService userCourseService;
    public UserCourseController(CourseClient userClient, UserService userService, UserCourseService userCourseService) {
        this.userClient = userClient;
        this.userService = userService;
        this.userCourseService = userCourseService;
    }
    @GetMapping("/users/{userId}/courses")
    public ResponseEntity<Page<CourseDto>> getAllCoursesByUser(@PageableDefault(page = 0, size = 10, sort = "courseId", direction = Sort.Direction.ASC) Pageable pageable,
                                                               @PathVariable UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userClient.getAllCoursesByUser(userId, pageable));
    }
    @PostMapping("/users/{userId}/courses/subscription")
    public ResponseEntity<Object> sabeSubscriptionUserInCourse(@PathVariable UUID userId,
                                                               @RequestBody @Valid UserCourseDTO userCourseDTO) {
        Optional<UserModel> userModelOptional = this.userService.findById(userId);
        if (!userModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        if (this.userCourseService.existsByUserAndCourseId(userModelOptional.get(), userCourseDTO.getCourseId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Subscription already exists;");
        }
        var userCouseModel = this.userCourseService.save(userModelOptional.get().convertToUserCourseModel(userCourseDTO.getCourseId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(userCouseModel);
    }
}
