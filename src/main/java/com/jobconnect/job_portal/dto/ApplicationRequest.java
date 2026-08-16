package com.jobconnect.job_portal.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApplicationRequest(
        @NotNull(message = "Job ID is required")
        Long jobId,

        @Size(max = 500)
        String resumeUrl,       // optional override; falls back to candidate's profile resume

        @Size(max = 2000)
        String coverLetter
) {}