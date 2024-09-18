package com.ead.authuser.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UserCourseDTO {

    private UUID userId;
    @NotNull
    private UUID courseId;

}
