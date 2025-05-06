//package com.institute.config;
//
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.*;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import com.institute.repository.InstitutionRepository;
//import com.institute.model.Institution;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.io.IOException;
//@Component
//public class TenantFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private InstitutionRepository institutionRepository;
//
////    @Override
////    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
////            throws ServletException, IOException {
////
////        String institutionIdentifier = request.getHeader("X-Institution-Identifier");
////
////        if (institutionIdentifier != null) {
////            Institution institution = institutionRepository.findByUsername(institutionIdentifier);
////            if (institution != null) {
////                TenantContext.setCurrentTenant(institution.getDbName());
////            }
////        }
////
////        try {
////            filterChain.doFilter(request, response);
////        } finally {
////            TenantContext.clear();
////        }
////    }
//@Override
//protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//        throws ServletException, IOException {
//
//    String institutionIdentifier = request.getHeader("X-Institution-Identifier");
//    System.out.println("Received X-Institution-Identifier: " + institutionIdentifier);
//
//    if (institutionIdentifier != null) {
//        Institution institution = institutionRepository.findByUsername(institutionIdentifier);
//        if (institution != null) {
//            System.out.println("Setting tenant: " + institution.getDbName());
//            TenantContext.setCurrentTenant(institution.getDbName());
//        } else {
//            System.out.println("Institution not found for: " + institutionIdentifier);
//        }
//    } else {
//        System.out.println("No X-Institution-Identifier header found in request.");
//    }
//
//    try {
//        filterChain.doFilter(request, response);
//    } finally {
//        TenantContext.clear();
//    }
//}
//
//}


package com.institute.config;

import com.institute.model.Institution;
import com.institute.repository.InstitutionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class TenantFilter extends OncePerRequestFilter {

    @Autowired
    private InstitutionRepository institutionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String institutionIdentifier = request.getHeader("X-Institution-Identifier");
        System.out.println("🔹 Received X-Institution-Identifier: " + institutionIdentifier);

        if (institutionIdentifier != null) {
            Optional<Institution> institution = institutionRepository.findByUsername(institutionIdentifier);
            if (institution.isPresent()) {
                System.out.println("✅ Setting Tenant: " + institution.get().getDbName());
                TenantContext.setCurrentTenant(institution.get().getDbName());
            } else {
                System.out.println("❌ Institution not found for: " + institutionIdentifier);
            }
        } else {
            System.out.println("❌ No X-Institution-Identifier header found in request.");
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            System.out.println("🧹 Clearing TenantContext");
            TenantContext.clear();
        }
    }
}
