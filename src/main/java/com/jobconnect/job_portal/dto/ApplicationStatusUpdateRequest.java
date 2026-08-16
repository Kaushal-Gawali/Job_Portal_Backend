package com.jobconnect.job_portal.dto;

import com.jobconnect.job_portal.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record ApplicationStatusUpdateRequest(
        @NotNull(message = "Status is required")
        ApplicationStatus status
) {}