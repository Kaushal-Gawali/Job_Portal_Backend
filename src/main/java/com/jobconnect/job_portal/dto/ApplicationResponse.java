package com.jobconnect.job_portal.dto;

import java.time.Instant;

public record ApplicationResponse(
        Long id,
        Long jobId,
        String jobTitle,
        String companyName,
        Long candidateId,
        String candidateName,
        String candidateEmail,
        String resumeUrl,
        String coverLetter,
        String status,
        Instant appliedAt
) {}