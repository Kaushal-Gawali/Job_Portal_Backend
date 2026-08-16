package com.jobconnect.job_portal.repository;

import com.jobconnect.job_portal.entity.Job;
import com.jobconnect.job_portal.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findByCompanyId(Long companyId, Pageable pageable);

    @Query("""
        SELECT j FROM Job j
        WHERE j.status = :status
        AND (:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%')))
        AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
        AND (:jobType IS NULL OR j.jobType = :jobType)
        """)
    Page<Job> searchJobs(
            @Param("status") JobStatus status,
            @Param("title") String title,
            @Param("location") String location,
            @Param("jobType") String jobType,
            Pageable pageable
    );
}