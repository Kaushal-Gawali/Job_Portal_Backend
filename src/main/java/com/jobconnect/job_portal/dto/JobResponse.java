package com.jobconnect.job_portal.dto;

import java.time.Instant;

public record JobResponse(
        Long id,
        String title,
        String description,
        String location,
        String jobType,
        Integer salaryMin,
        Integer salaryMax,
        String skillsRequired,
        String status,
        Instant createdAt,
        Long companyId,
        String companyName
) {}