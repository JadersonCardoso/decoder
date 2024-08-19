package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/auth")
public class AuthenticationController {

//    Logger logger = LogManager.getLogger(AuthenticationController.class);

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody
                                              @Validated(UserDto.UserView.RegistrationPost.class)
                                              @JsonView(UserDto.UserView.RegistrationPost.class) UserDto userDto) {
        log.debug("POST registerUser userDto received {} ",userDto.toString());
        if (this.userService.existsByUsername(userDto.getUserName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Username is Already Taken!");
        }
        if (this.userService.existsByEmail(userDto.getEmail())) {
            log.warn("Email {} is Already Taken. ",userDto.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Email is Already Taken!");
        }

        var userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel);
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.STUDENT);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        this.userService.save(userModel);
        log.debug("POST registerUser userModel received {} ",userModel.toString());
        log.info("User saved successfully userId {} ",userModel.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }
    @GetMapping("/")
    public String index() {
        log.trace("TRACE"); //detalhado
        log.debug("DEBUG"); //ambiente de desenvolvimento
        log.info("INFO"); // ambiente de produção
        log.warn("WARN"); // informações de imcompatibilidade
        log.error("ERROR"); //erro durante o processo,detalhes do erro gerado, blocos try catch
        return "Logginf Strong Boot...";
    }

}
