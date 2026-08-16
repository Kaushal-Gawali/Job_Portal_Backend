package com.jobconnect.job_portal.dto;

public record CandidateProfileResponse(
        Long id,
        String name,
        String email,
        String phone,
        String skills,
        String resumeUrl,
        Integer experienceYears,
        String location
) {}