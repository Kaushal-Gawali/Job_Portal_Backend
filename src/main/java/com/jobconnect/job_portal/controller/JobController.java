package com.jobconnect.job_portal.controller;

import com.jobconnect.job_portal.dto.*;
import com.jobconnect.job_portal.enums.JobStatus;
import com.jobconnect.job_portal.security.UserPrincipal;
import com.jobconnect.job_portal.service.JobService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // ---------- Public ----------

    @GetMapping
    public PagedResponse<JobResponse> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return jobService.searchJobs(title, location, jobType, pageable);
    }

    @GetMapping("/{id}")
    public JobResponse getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }

    // ---------- Employer-only ----------

    @GetMapping("/my-jobs")
    public PagedResponse<JobResponse> getMyJobs(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return jobService.getMyJobs(principal.getUsername(), pageable);
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody JobRequest request
    ) {
        JobResponse response = jobService.createJob(principal.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public JobResponse updateJob(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request
    ) {
        return jobService.updateJob(principal.getUsername(), id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        jobService.deleteJob(principal.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public JobResponse updateJobStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @RequestParam JobStatus status
    ) {
        return jobService.updateJobStatus(principal.getUsername(), id, status);
    }
}