package com._blog.backend.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    
    @Size(min = 2, max = 20, message = "First name must be between 2 and 20 characters")
    private String firstName;
    
    @Size(min = 2, max = 20, message = "Last name must be between 2 and 20 characters")
    private String lastName;
    
    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;
}
