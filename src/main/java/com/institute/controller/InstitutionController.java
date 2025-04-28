package com.institute.controller;

import com.institute.model.Institution;
import com.institute.service.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/institutions")
@CrossOrigin(origins = "http://localhost:3000") // 👈 Allow React frontend
public class InstitutionController {

    @Autowired
    private InstitutionService institutionService;

    // Register new institution
    @PostMapping("/register")
    public Institution register(@RequestBody Institution institution) {
        return institutionService.registerInstitution(institution);
    }

    // Login for institution
    @PostMapping("/login")
    public Institution
    login(@RequestParam String email, @RequestParam String password) {
        return institutionService.login(email, password);
    }

    // Update email for institution
    @PutMapping("/{id}/update-email")
    public Institution updateEmail(@PathVariable Long id, @RequestParam String email) {
        return institutionService.updateEmail(id, email);
    }
}
