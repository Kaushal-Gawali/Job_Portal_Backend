package com.jobconnect.job_portal.dto;

public record CompanyResponse(
        Long id,
        String name,
        String description,
        String website,
        String logoUrl,
        String ownerEmail
) {}