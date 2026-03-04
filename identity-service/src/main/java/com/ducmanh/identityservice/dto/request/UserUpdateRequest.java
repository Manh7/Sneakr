package com.ducmanh.identityservice.dto.request;

import com.ducmanh.identityservice.validator.DobConstraint;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest
{
    @Size(min = 8, message = "PASSWORD_INVALID")
    private String password;

    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "EMAIL_INVALID"
    )
    private String email;

    @DobConstraint(min = 18, message = "DOB_INVALID")
    private LocalDate dob;

    private String address;

    private List<String> roles;
}
