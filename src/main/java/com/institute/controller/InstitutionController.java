package com.institute.controller;

import com.institute.model.Institution;
import com.institute.service.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


//@CrossOrigin(
//        origins = "http://localhost:3000",
//        allowedHeaders = {"X-Institution-Identifier", "Content-Type"},
//        exposedHeaders = {"X-Institution-Identifier"}
//)
@CrossOrigin(origins = "http://localhost:3000") // 👈 Allow React frontend
@RestController
@RequestMapping("/institutions")
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
    // Login for institution
    @PutMapping("/update")
    public Institution update(@RequestParam String email, @RequestBody Institution institution) {
        return institutionService.updateInstitute(email, institution);
    }

    // Update email for institution
    @PutMapping("/{id}/update-email")
    public Institution updateEmail(@PathVariable Long id, @RequestParam String email) {
        return institutionService.updateEmail(id, email);
    }
    // ✅ Update institution by ID (new method)
    @PutMapping("/{id}/update-all")
    public Institution updateInstitutionById(@PathVariable Long id, @RequestBody Institution institution) {
        return institutionService.updateInstitutionById(id, institution);
    }
}
