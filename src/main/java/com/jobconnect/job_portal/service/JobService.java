package com.jobconnect.job_portal.service;

import com.jobconnect.job_portal.dto.*;
import com.jobconnect.job_portal.entity.Company;
import com.jobconnect.job_portal.entity.Job;
import com.jobconnect.job_portal.entity.User;
import com.jobconnect.job_portal.enums.JobStatus;
import com.jobconnect.job_portal.exception.ResourceNotFoundException;
import com.jobconnect.job_portal.repository.CompanyRepository;
import com.jobconnect.job_portal.repository.JobRepository;
import com.jobconnect.job_portal.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository,
                      CompanyRepository companyRepository,
                      UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    // ---------- Public search/browse ----------

    public PagedResponse<JobResponse> searchJobs(String title, String location, String jobType, Pageable pageable) {
        Page<Job> jobs = jobRepository.searchJobs(JobStatus.OPEN, title, location, jobType, pageable);
        return PagedResponse.from(jobs.map(this::toResponse));
    }

    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        return toResponse(job);
    }

    // ---------- Employer-only management ----------

    @Transactional
    public JobResponse createJob(String employerEmail, JobRequest request) {
        Company company = getCompanyByOwnerEmail(employerEmail);

        Job job = Job.builder()
                .company(company)
                .title(request.title())
                .description(request.description())
                .location(request.location())
                .jobType(request.jobType())
                .salaryMin(request.salaryMin())
                .salaryMax(request.salaryMax())
                .skillsRequired(request.skillsRequired())
                .status(JobStatus.OPEN)
                .build();

        job = jobRepository.save(job);
        return toResponse(job);
    }

    @Transactional
    public JobResponse updateJob(String employerEmail, Long jobId, JobRequest request) {
        Job job = getJobOwnedBy(employerEmail, jobId);

        job.setTitle(request.title());
        job.setDescription(request.description());
        job.setLocation(request.location());
        job.setJobType(request.jobType());
        job.setSalaryMin(request.salaryMin());
        job.setSalaryMax(request.salaryMax());
        job.setSkillsRequired(request.skillsRequired());

        jobRepository.save(job);
        return toResponse(job);
    }

    @Transactional
    public void deleteJob(String employerEmail, Long jobId) {
        Job job = getJobOwnedBy(employerEmail, jobId);
        jobRepository.delete(job);
    }

    @Transactional
    public JobResponse updateJobStatus(String employerEmail, Long jobId, JobStatus status) {
        Job job = getJobOwnedBy(employerEmail, jobId);
        job.setStatus(status);
        jobRepository.save(job);
        return toResponse(job);
    }

    public PagedResponse<JobResponse> getMyJobs(String employerEmail, Pageable pageable) {
        Company company = getCompanyByOwnerEmail(employerEmail);
        Page<Job> jobs = jobRepository.findByCompanyId(company.getId(), pageable);
        return PagedResponse.from(jobs.map(this::toResponse));
    }

    // ---------- Helpers ----------

    /**
     * Fetches a job and verifies the given employer's company owns it.
     * Throws 404 if the job doesn't exist, 403 if it exists but belongs to someone else.
     */
    private Job getJobOwnedBy(String employerEmail, Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        Company company = getCompanyByOwnerEmail(employerEmail);

        if (!job.getCompany().getId().equals(company.getId())) {
            throw new AccessDeniedException("You do not have permission to modify this job");
        }

        return job;
    }

    private Company getCompanyByOwnerEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return companyRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found for this user"));
    }

    private JobResponse toResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getLocation(),
                job.getJobType(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getSkillsRequired(),
                job.getStatus().name(),
                job.getCreatedAt(),
                job.getCompany().getId(),
                job.getCompany().getName()
        );
    }
}