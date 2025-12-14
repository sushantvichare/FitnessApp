package com.fitness.userservice.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email is Invalid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 30 ,message = "Password should be 6-30 chars.")
    private String password;

    private String keyCloakId;

    private String firstName;
    private String lastName;



}
