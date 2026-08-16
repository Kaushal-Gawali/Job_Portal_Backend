// controller/CompanyController.java
package com.jobconnect.job_portal.controller;

import com.jobconnect.job_portal.dto.CompanyRequest;
import com.jobconnect.job_portal.dto.CompanyResponse;
import com.jobconnect.job_portal.security.UserPrincipal;
import com.jobconnect.job_portal.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/me")
    public CompanyResponse getMyCompany(@AuthenticationPrincipal UserPrincipal principal) {
        return companyService.getMyCompany(principal.getUsername());
    }

    @PutMapping("/me")
    public CompanyResponse updateMyCompany(@AuthenticationPrincipal UserPrincipal principal,
                                           @Valid @RequestBody CompanyRequest request) {
        return companyService.updateMyCompany(principal.getUsername(), request);
    }
}