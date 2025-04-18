package com.pms.controller;

import com.pms.entity.Policy;
import com.pms.entity.Scheme;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/schemes")
public class SchemeUIController {

	@Autowired
	private RestTemplate restTemplate;
    private final String baseUrl;

    public SchemeUIController(RestTemplate restTemplate,
                              @Value("${backend.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @GetMapping("/view")
    public String getSchemeByID(@RequestParam(name = "id", required = false) Long id, Model model) {
        if (id == null) {
            model.addAttribute("errorMessage", "Please enter a valid Scheme ID.");
            model.addAttribute("schemes", List.of());
            return "scheme-list";
        }
        try {
            Scheme scheme = restTemplate.getForObject(baseUrl + "/{id}", Scheme.class, id);
            model.addAttribute("schemes", List.of(scheme));
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Scheme not found.");
            model.addAttribute("schemes", List.of());
        }
        return "scheme-list";
    }

    @GetMapping
    public String getAllSchemes(Model model) {
        try {
            Scheme[] schemesArray = restTemplate.getForObject(baseUrl, Scheme[].class);
            List<Scheme> schemes = Arrays.asList(schemesArray);
            model.addAttribute("schemes", schemes);
        } catch (Exception e) {
            model.addAttribute("errorMessages", Arrays.asList("Failed to load schemes: " + e.getMessage()));
        }
        return "scheme-list";
    }

    @GetMapping("/{schemeId}/policies")

    public String getPoliciesBySchemeId(@PathVariable int schemeId, Model model) {

        try {

            Policy[] policiesArray = restTemplate.getForObject(baseUrl + "/{schemeId}/policies", Policy[].class, schemeId);

            List<Policy> policies = Arrays.asList(policiesArray);

            model.addAttribute("policies", policies);

        } catch (Exception e) {

            model.addAttribute("errorMessages", Arrays.asList("Failed to load policies: " + e.getMessage()));

            model.addAttribute("policies", List.of());

        }

        return "policy-list";

    }
    
    @GetMapping("/linked-policies/{schemeId}")

    public String viewLinkedPolicies(@PathVariable int schemeId, Model model) {

        try {

            Policy[] policiesArray = restTemplate.getForObject(baseUrl + "/{schemeId}/policies", Policy[].class, schemeId);

            List<Policy> policies = Arrays.asList(policiesArray);

            model.addAttribute("policies", policies);

            model.addAttribute("schemeId", schemeId);

        } catch (Exception e) {

            model.addAttribute("errorMessage", "Failed to load policies: " + e.getMessage());

            model.addAttribute("policies", List.of());

        }

        return "policy-list";

    }
    
    @GetMapping("/search")
    public String searchSchemeByName(@RequestParam(name = "schemeName", required = false) String schemeName, Model model) {
        if (schemeName == null || schemeName.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Please enter a valid scheme name.");
            model.addAttribute("schemes", List.of()); // Empty List to Avoid Error
            return "scheme-list";
        }
        try {
            Scheme[] schemesArray = restTemplate.getForObject(baseUrl + "/search?schemeName={schemeName}", Scheme[].class, schemeName);
            List<Scheme> schemes = Arrays.asList(schemesArray);

            if (schemes.isEmpty()) {
                model.addAttribute("errorMessage", "No schemes found with name: " + schemeName);
                model.addAttribute("schemes", List.of()); // Empty List
            } else {
                model.addAttribute("schemes", schemes);
            }
        } catch (HttpClientErrorException.NotFound e) {
            model.addAttribute("errorMessage", "No schemes found with name: " + schemeName);
            model.addAttribute("schemes", List.of());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to search schemes: " + e.getMessage());
            model.addAttribute("schemes", List.of());
        }
        return "scheme-list";
    }

    // Show create form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("scheme", new Scheme());
        return "scheme-form";
    }

    // Show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        try {
            Scheme scheme = restTemplate.getForObject(baseUrl + "/{id}", Scheme.class, id);
            model.addAttribute("scheme", scheme);
        } catch (Exception e) {
            model.addAttribute("errorMessages", Arrays.asList("Failed to load scheme details: " + e.getMessage()));
            return "scheme-list";
        }
        return "scheme-form";
    }

    // Save new scheme
    @PostMapping("/save")
    public String saveScheme(@ModelAttribute Scheme scheme, Model model) {
        try {
            restTemplate.postForObject(baseUrl, scheme, Scheme.class);
            return "redirect:/schemes";
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            model.addAttribute("errorMessages", Arrays.asList(responseBody.replace("{", "").replace("}", "").split(",")));
            model.addAttribute("scheme", scheme);
            return "scheme-form";
        } catch (Exception e) {
            model.addAttribute("errorMessages", Arrays.asList("Unexpected error: " + e.getMessage()));
            model.addAttribute("scheme", scheme);
            return "scheme-form";
        }
    }

    // Update existing scheme
    @PutMapping("/update/{id}")
    public String updateScheme(@PathVariable int id, @ModelAttribute Scheme scheme, Model model) {
        try {
            restTemplate.put(baseUrl + "/{id}", scheme, id);
            return "redirect:/schemes";
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            model.addAttribute("errorMessages", Arrays.asList(responseBody.replace("{", "").replace("}", "").split(",")));
            model.addAttribute("scheme", scheme);
            return "scheme-form";
        } catch (Exception e) {
            model.addAttribute("errorMessages", Arrays.asList("Unexpected error: " + e.getMessage()));
            model.addAttribute("scheme", scheme);
            return "scheme-form";
        }
    }

    // Toggle scheme status
    @GetMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable int id, @RequestParam boolean isActive, Model model) {
        try {
            ResponseEntity<Scheme> response = restTemplate.exchange(
                    baseUrl + "/{id}/status?isActive={isActive}",
                    HttpMethod.PUT,
                    null,
                    Scheme.class,
                    id,
                    isActive
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Status toggled successfully.");
            } else {
                model.addAttribute("errorMessages", Arrays.asList("Failed to toggle status: " + response.getStatusCode()));
            }
        } catch (Exception e) {
            model.addAttribute("errorMessages", Arrays.asList("Error toggling status: " + e.getMessage()));
        }

        return "redirect:/schemes";
    }
}
