package com.jobconnect.job_portal.dto;

public record AdminStatsResponse(
        long totalUsers,
        long totalCandidates,
        long totalEmployers,
        long totalJobs,
        long openJobs,
        long pendingJobs,
        long totalApplications
) {}