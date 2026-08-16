package com.jobconnect.job_portal.service;

import com.jobconnect.job_portal.dto.*;
import com.jobconnect.job_portal.entity.*;
import com.jobconnect.job_portal.enums.ApplicationStatus;
import com.jobconnect.job_portal.exception.DuplicateResourceException;
import com.jobconnect.job_portal.exception.ResourceNotFoundException;
import com.jobconnect.job_portal.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              JobRepository jobRepository,
                              CandidateProfileRepository candidateProfileRepository,
                              CompanyRepository companyRepository,
                              UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.candidateProfileRepository = candidateProfileRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    // ---------- Candidate actions ----------

    @Transactional
    public ApplicationResponse apply(String candidateEmail, ApplicationRequest request) {
        CandidateProfile candidate = getCandidateByEmail(candidateEmail);

        Job job = jobRepository.findById(request.jobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + request.jobId()));

        if (applicationRepository.existsByJobIdAndCandidateId(job.getId(), candidate.getId())) {
            throw new DuplicateResourceException("You have already applied to this job");
        }

        String resumeUrl = (request.resumeUrl() != null && !request.resumeUrl().isBlank())
                ? request.resumeUrl()
                : candidate.getResumeUrl();

        Application application = Application.builder()
                .job(job)
                .candidate(candidate)
                .resumeUrl(resumeUrl)
                .coverLetter(request.coverLetter())
                .status(ApplicationStatus.APPLIED)
                .build();

        application = applicationRepository.save(application);
        return toResponse(application);
    }

    public PagedResponse<ApplicationResponse> getMyApplications(String candidateEmail, Pageable pageable) {
        CandidateProfile candidate = getCandidateByEmail(candidateEmail);
        Page<Application> applications = applicationRepository.findByCandidateId(candidate.getId(), pageable);
        return PagedResponse.from(applications.map(this::toResponse));
    }

    // ---------- Employer actions ----------

    public PagedResponse<ApplicationResponse> getApplicationsForJob(String employerEmail, Long jobId, Pageable pageable) {
        Job job = getJobOwnedByEmployer(employerEmail, jobId);
        Page<Application> applications = applicationRepository.findByJobId(job.getId(), pageable);
        return PagedResponse.from(applications.map(this::toResponse));
    }

    @Transactional
    public ApplicationResponse updateStatus(String employerEmail, Long applicationId, ApplicationStatus newStatus) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        // Verify the employer owns the job this application belongs to
        getJobOwnedByEmployer(employerEmail, application.getJob().getId());

        application.setStatus(newStatus);
        applicationRepository.save(application);
        return toResponse(application);
    }

    // ---------- Helpers ----------

    private CandidateProfile getCandidateByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));
    }

    private Job getJobOwnedByEmployer(String employerEmail, Long jobId) {
        User user = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Company company = companyRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found for this user"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.getCompany().getId().equals(company.getId())) {
            throw new AccessDeniedException("You do not have permission to access applications for this job");
        }

        return job;
    }

    private ApplicationResponse toResponse(Application app) {
        return new ApplicationResponse(
                app.getId(),
                app.getJob().getId(),
                app.getJob().getTitle(),
                app.getJob().getCompany().getName(),
                app.getCandidate().getId(),
                app.getCandidate().getUser().getName(),
                app.getCandidate().getUser().getEmail(),
                app.getResumeUrl(),
                app.getCoverLetter(),
                app.getStatus().name(),
                app.getAppliedAt()
        );
    }
}