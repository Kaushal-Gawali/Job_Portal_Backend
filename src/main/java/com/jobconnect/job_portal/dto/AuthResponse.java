package com.jobconnect.job_portal.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String email,
        String role,
        String name
) {}