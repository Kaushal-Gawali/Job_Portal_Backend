package com.jobconnect.job_portal.service;

import com.jobconnect.job_portal.dto.*;
import com.jobconnect.job_portal.entity.Job;
import com.jobconnect.job_portal.entity.User;
import com.jobconnect.job_portal.enums.JobStatus;
import com.jobconnect.job_portal.enums.Role;
import com.jobconnect.job_portal.exception.ResourceNotFoundException;
import com.jobconnect.job_portal.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public AdminService(UserRepository userRepository,
                        JobRepository jobRepository,
                        ApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    public PagedResponse<AdminUserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return PagedResponse.from(users.map(this::toUserResponse));
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot delete an admin account through this endpoint");
        }

        userRepository.delete(user);
        // Cascades to CandidateProfile/Company/Jobs/Applications via ON DELETE CASCADE in the schema
    }

    @Transactional
    public JobResponse updateJobStatus(Long jobId, JobStatus status) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        job.setStatus(status);
        jobRepository.save(job);
        return toJobResponse(job);
    }

    public AdminStatsResponse getStats() {
        long totalUsers = userRepository.count();
        long totalCandidates = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.CANDIDATE).count();
        long totalEmployers = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.EMPLOYER).count();
        long totalJobs = jobRepository.count();
        long openJobs = jobRepository.findAll().stream()
                .filter(j -> j.getStatus() == JobStatus.OPEN).count();
        long pendingJobs = jobRepository.findAll().stream()
                .filter(j -> j.getStatus() == JobStatus.PENDING_APPROVAL).count();
        long totalApplications = applicationRepository.count();

        return new AdminStatsResponse(totalUsers, totalCandidates, totalEmployers,
                totalJobs, openJobs, pendingJobs, totalApplications);
    }

    private AdminUserResponse toUserResponse(User user) {
        return new AdminUserResponse(user.getId(), user.getName(), user.getEmail(),
                user.getRole().name(), user.getCreatedAt());
    }

    private JobResponse toJobResponse(Job job) {
        return new JobResponse(
                job.getId(), job.getTitle(), job.getDescription(), job.getLocation(),
                job.getJobType(), job.getSalaryMin(), job.getSalaryMax(), job.getSkillsRequired(),
                job.getStatus().name(), job.getCreatedAt(), job.getCompany().getId(), job.getCompany().getName()
        );
    }
}