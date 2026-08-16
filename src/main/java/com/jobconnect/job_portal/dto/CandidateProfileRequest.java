package com.jobconnect.job_portal.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record CandidateProfileRequest(
        @Size(max = 30) String phone,
        String skills,
        @Size(max = 500) String resumeUrl,
        @Min(0) Integer experienceYears,
        @Size(max = 150) String location
) {}