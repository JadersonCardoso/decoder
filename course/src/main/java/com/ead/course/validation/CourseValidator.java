package com.ead.course.validation;

import com.ead.course.dtos.CourseDto;
import com.ead.course.enums.UserType;
import com.ead.course.models.UserModel;
import com.ead.course.services.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;
import java.util.UUID;

@Component
public class CourseValidator implements Validator {
    @Qualifier("defaultValidator")
    private final Validator validator;
    private final UserService userService;


    public CourseValidator(Validator validator, UserService userService) {
        this.validator = validator;
        this.userService = userService;
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
        Optional<UserModel> userModelOptional = userService.findById(userInstructor);
        if (!userModelOptional.isPresent()) {
            errors.rejectValue("userInstructor", "UserInsctructorError", "Instructor not found.");
        }
        if (userModelOptional.get().getUserType().equals(UserType.STUDENT.toString())) {
            errors.rejectValue("userInstructor", "UserInsctructorError", "User must be INSTRUCTOR or ADMIN.");
        }
    }
}
