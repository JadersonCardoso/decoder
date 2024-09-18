package com.ead.authuser.controllers;

import com.ead.authuser.dtos.InstructoDto;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import jakarta.validation.Valid;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/instructors")
public class InstructorController {

    private final UserService userService;
    public InstructorController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/subscription")
    ResponseEntity<Object> saveSubscriptionInstructor(@RequestBody @Valid InstructoDto instructoDto) {
        Optional<UserModel> userModelOptional = this.userService.findById(instructoDto.getUserId());
        if (!userModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } else {
            var userModel = userModelOptional.get();
            userModel.setUserType(UserType.INSTRUCTOR);
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
            this.userService.save(userModel);
            return ResponseEntity.ok(userModel);
        }
    }
}
