package com.jobconnect.job_portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyRequest(
        @NotBlank @Size(max = 200) String name,
        String description,
        @Size(max = 300) String website,
        @Size(max = 500) String logoUrl
) {}