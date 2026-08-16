// controller/CandidateController.java
package com.jobconnect.job_portal.controller;

import com.jobconnect.job_portal.dto.CandidateProfileRequest;
import com.jobconnect.job_portal.dto.CandidateProfileResponse;
import com.jobconnect.job_portal.security.UserPrincipal;
import com.jobconnect.job_portal.service.CandidateProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private final CandidateProfileService candidateProfileService;

    public CandidateController(CandidateProfileService candidateProfileService) {
        this.candidateProfileService = candidateProfileService;
    }

    @GetMapping("/me")
    public CandidateProfileResponse getMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return candidateProfileService.getMyProfile(principal.getUsername());
    }

    @PutMapping("/me")
    public CandidateProfileResponse updateMyProfile(@AuthenticationPrincipal UserPrincipal principal,
                                                    @Valid @RequestBody CandidateProfileRequest request) {
        return candidateProfileService.updateMyProfile(principal.getUsername(), request);
    }

    @PostMapping(value = "/me/resume", consumes = "multipart/form-data")
    public CandidateProfileResponse uploadResume(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file
    ) {
        return candidateProfileService.uploadResume(principal.getUsername(), file);
    }
}