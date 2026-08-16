package com.jobconnect.job_portal.service;

import com.jobconnect.job_portal.dto.CompanyRequest;
import com.jobconnect.job_portal.dto.CompanyResponse;
import com.jobconnect.job_portal.entity.Company;
import com.jobconnect.job_portal.entity.User;
import com.jobconnect.job_portal.exception.ResourceNotFoundException;
import com.jobconnect.job_portal.repository.CompanyRepository;
import com.jobconnect.job_portal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public CompanyResponse getMyCompany(String email) {
        User user = getUserByEmail(email);
        Company company = companyRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return toResponse(company);
    }

    @Transactional
    public CompanyResponse updateMyCompany(String email, CompanyRequest request) {
        User user = getUserByEmail(email);
        Company company = companyRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        company.setName(request.name());
        company.setDescription(request.description());
        company.setWebsite(request.website());
        company.setLogoUrl(request.logoUrl());

        companyRepository.save(company);
        return toResponse(company);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private CompanyResponse toResponse(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getDescription(),
                company.getWebsite(),
                company.getLogoUrl(),
                company.getOwner().getEmail()
        );
    }
}