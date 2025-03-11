package com.institute.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.institute.repository.InstitutionRepository;
import com.institute.model.Institution;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
@Component
public class TenantFilter extends OncePerRequestFilter {

    @Autowired
    private InstitutionRepository institutionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String institutionIdentifier = request.getHeader("X-Institution-Identifier");

        if (institutionIdentifier != null) {
            Institution institution = institutionRepository.findByUsername(institutionIdentifier);
            if (institution != null) {
                TenantContext.setCurrentTenant(institution.getDbName());
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
