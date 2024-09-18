package com.ead.course.validation;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.dtos.CourseDto;
import com.ead.course.dtos.UserDto;
import com.ead.course.enums.UserType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.UUID;

@Component
public class CourseValidator implements Validator {
    @Qualifier("defaultValidator")
    private final Validator validator;

    private final AuthUserClient authUserClient;

    public CourseValidator(Validator validator, AuthUserClient authUserClient) {
        this.validator = validator;
        this.authUserClient = authUserClient;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CourseDto courseDto = (CourseDto) o;
        validator.validate(courseDto, errors);
        if (!errors.hasErrors()) {
            validateUserInstrcutor(courseDto.getUserInstructor(), errors);
        }
    }

    private void validateUserInstrcutor(UUID userInstructor, Errors errors) {
        ResponseEntity<UserDto> responseUserInstructor;
        try {
            responseUserInstructor = this.authUserClient.getOneUserById(userInstructor);
            if (responseUserInstructor.getBody().getUserType().equals(UserType.STUDENT)) {
                errors.rejectValue("userInstructor", "UserInsctructorError", "User must be INSTRUCTOR or ADMIN.");
            }
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                errors.rejectValue("userInstructor", "UserInsctructorError", "Instructor not found.");
            }
        }
    }
}
