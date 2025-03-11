package com.institute.controller;

import com.institute.model.Institution;
import com.institute.service.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/institutions")
public class InstitutionController {
    @Autowired
    private InstitutionService institutionService;

    @PostMapping("/register")
    public Institution register(@RequestBody Institution institution) {
        return institutionService.registerInstitution(institution);
    }
}
