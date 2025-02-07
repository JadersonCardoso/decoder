package com.ead.course.validation;

import com.ead.course.dtos.CourseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.UUID;

@Component
public class CourseValidator implements Validator {
    @Qualifier("defaultValidator")
    private final Validator validator;

    public CourseValidator(Validator validator) {
        this.validator = validator;
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
//        ResponseEntity<UserDto> responseUserInstructor;
//        try {
//            responseUserInstructor = this.authUserClient.getOneUserById(userInstructor);
//            if (responseUserInstructor.getBody().getUserType().equals(UserType.STUDENT)) {
//                errors.rejectValue("userInstructor", "UserInsctructorError", "User must be INSTRUCTOR or ADMIN.");
//            }
//        } catch (HttpStatusCodeException e) {
//            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
//                errors.rejectValue("userInstructor", "UserInsctructorError", "Instructor not found.");
//            }
//        }
    }
}
