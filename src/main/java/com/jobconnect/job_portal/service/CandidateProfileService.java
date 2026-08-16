package com.jobconnect.job_portal.service;

import com.jobconnect.job_portal.dto.CandidateProfileRequest;
import com.jobconnect.job_portal.dto.CandidateProfileResponse;
import com.jobconnect.job_portal.entity.CandidateProfile;
import com.jobconnect.job_portal.entity.User;
import com.jobconnect.job_portal.exception.ResourceNotFoundException;
import com.jobconnect.job_portal.repository.CandidateProfileRepository;
import com.jobconnect.job_portal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CandidateProfileService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public CandidateProfileService(CandidateProfileRepository candidateProfileRepository,
                                   UserRepository userRepository,
                                   FileStorageService fileStorageService) {
        this.candidateProfileRepository = candidateProfileRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    public CandidateProfileResponse getMyProfile(String email) {
        User user = getUserByEmail(email);
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));
        return toResponse(profile, user);
    }

    @Transactional
    public CandidateProfileResponse updateMyProfile(String email, CandidateProfileRequest request) {
        User user = getUserByEmail(email);
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

        profile.setPhone(request.phone());
        profile.setSkills(request.skills());
        profile.setResumeUrl(request.resumeUrl());
        profile.setExperienceYears(request.experienceYears());
        profile.setLocation(request.location());

        candidateProfileRepository.save(profile);
        return toResponse(profile, user);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private CandidateProfileResponse toResponse(CandidateProfile profile, User user) {
        return new CandidateProfileResponse(
                profile.getId(),
                user.getName(),
                user.getEmail(),
                profile.getPhone(),
                profile.getSkills(),
                profile.getResumeUrl(),
                profile.getExperienceYears(),
                profile.getLocation()
        );
    }

    @Transactional
    public CandidateProfileResponse uploadResume(String email, MultipartFile file) {
        User user = getUserByEmail(email);
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

        String storedFilename = fileStorageService.storeFile(file, profile.getId());
        String resumeUrl = "/uploads/resumes/" + storedFilename;

        profile.setResumeUrl(resumeUrl);
        candidateProfileRepository.save(profile);

        return toResponse(profile, user);
    }
}