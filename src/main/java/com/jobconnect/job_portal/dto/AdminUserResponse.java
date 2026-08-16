package com.jobconnect.job_portal.dto;

import java.time.Instant;

public record AdminUserResponse(
        Long id,
        String name,
        String email,
        String role,
        Instant createdAt
) {}