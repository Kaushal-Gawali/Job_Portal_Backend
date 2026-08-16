package com.jobconnect.job_portal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "candidate_profiles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CandidateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String skills;          // comma-separated for MVP

    @Column(name = "resume_url")
    private String resumeUrl;

    @Column(name = "experience_years")
    private Integer experienceYears;

    private String location;
}