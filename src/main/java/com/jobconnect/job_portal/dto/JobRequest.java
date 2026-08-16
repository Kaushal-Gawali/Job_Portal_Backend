package com.jobconnect.job_portal.dto;

import jakarta.validation.constraints.*;

public record JobRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200)
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @Size(max = 150)
        String location,

        @Size(max = 30)
        String jobType,        // FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP

        @Min(0)
        Integer salaryMin,

        @Min(0)
        Integer salaryMax,

        String skillsRequired
) {}