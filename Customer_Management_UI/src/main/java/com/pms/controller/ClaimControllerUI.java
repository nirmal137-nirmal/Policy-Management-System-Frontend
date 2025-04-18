package com.pms.controller;

import com.pms.entity.Claim;
import com.pms.entity.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/claims")
public class ClaimControllerUI {

    private final String BASE_URL = "http://localhost:8030/api/claims"; // Match the backend API

    @Autowired
    private RestTemplate restTemplate;

    // Load homepage with an empty claim object
    @GetMapping("/index")
    public String showHomePage(Model model) {
        Claim claim = new Claim();
        claim.setPolicy(new Policy()); 
        model.addAttribute("claim", claim);
        return "index";
    }
    @GetMapping("/create")
    public String showCreateClaimPage(Model model) {
        Claim claim = new Claim();
        claim.setPolicy(new Policy()); 
        model.addAttribute("claim", claim);
        return "create-claim";
    }

    @PostMapping("/createClaim")
    public String createClaim(@Valid @ModelAttribute("claim") Claim claim, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Please fix the validation errors.");
            return "create-claim"; 
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Claim> request = new HttpEntity<>(claim, headers);

            ResponseEntity<Claim> response = restTemplate.postForEntity(BASE_URL, request, Claim.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("successMessage", "Claim created successfully!");
            } else {
                model.addAttribute("errorMessage", "Failed to create claim.");
            }
        } catch (HttpClientErrorException e) {
            model.addAttribute("errorMessage", "Backend error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
        }

        return "create-claim";
    }
    // Fetch a single claim by ID
    @GetMapping("/getClaimById")
    public String getClaimById(@RequestParam("claimId") Long claimId, Model model) {
        try {
            String url = BASE_URL + "/" + claimId;
            ResponseEntity<Claim> response = restTemplate.getForEntity(url, Claim.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("claim", response.getBody());
            } else {
                model.addAttribute("claim", new Claim()); // D
                model.addAttribute("errorMessage", "Claim not found with ID: " + claimId);
            }
        } catch (HttpClientErrorException.NotFound e) {
            model.addAttribute("claim", new Claim());
            model.addAttribute("errorMessage", "Claim not found with ID: " + claimId);
        } catch (Exception e) {
            model.addAttribute("claim", new Claim());
            model.addAttribute("errorMessage", "An error occurred: " + e.getMessage());
        }

        return "update-claim";
    }

    @PostMapping("/update/{claimId}")
    public String updateClaim(@PathVariable Long claimId, 
                              @Valid @ModelAttribute("claim") Claim claim, 
                              BindingResult result, 
                              Model model) {

        try {
            String url = BASE_URL + "/" + claimId;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Claim> request = new HttpEntity<>(claim, headers);
            restTemplate.exchange(url, HttpMethod.PUT, request, Claim.class);

            model.addAttribute("successMessage", "Claim updated successfully!");
        } catch (HttpClientErrorException.NotFound e) {
            model.addAttribute("errorMessage", "Claim not found with ID: " + claimId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred: " + e.getMessage());
            e.printStackTrace(); // Log full error
        }

        return "update-claim";
    }

    // Fetch claims by policy ID
    @GetMapping("/policy")
    public String getClaimsByPolicyId(@RequestParam(name = "policyId", required = false) String policyId, Model model) {
        try {
            if (policyId == null) {
                model.addAttribute("errorMessage", "Policy ID is required.");
                return "view-claim";
            }

            // Call the backend API to fetch claims by policyId
            ResponseEntity<Claim[]> response = restTemplate.getForEntity(BASE_URL + "/policy/" + policyId, Claim[].class);

            if (response.getStatusCode().is2xxSuccessful()) {
                List<Claim> claims = Arrays.asList(response.getBody());
                model.addAttribute("claims", claims);
                model.addAttribute("policyId", policyId);
            } else {
                model.addAttribute("errorMessage", "No claims found for Policy ID: " + policyId);
            }
        } catch (HttpClientErrorException.NotFound e) {
            // Handle 404 Not Found
            model.addAttribute("errorMessage", "No claims found for Policy ID: " + policyId);
        } catch (Exception e) {
            // Handle other exceptions
            model.addAttribute("errorMessage", "An error occurred: " + e.getMessage());
        }

        return "view-claim";
    }
    
    @PostMapping("/delete")
    public String deleteClaim(@RequestParam Long claimId, Model model) {
        try {
            String url = BASE_URL + "/" + claimId;
            restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
            model.addAttribute("successMessage", "Claim deleted successfully!");
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                model.addAttribute("errorMessage", "Claim not found with ID:"+claimId);
            } else {
                model.addAttribute("errorMessage", "An error occurred while deleting the claim.");
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An unexpected error occurred.");
        }
        model.addAttribute("claim", new Claim());
        return "create-claim";
    }



    @GetMapping("/all")
    public String getAllClaims(Model model) {
        try {
            ResponseEntity<Claim[]> response = restTemplate.getForEntity(BASE_URL, Claim[].class);
            List<Claim> allClaims = Arrays.asList(response.getBody());

            model.addAttribute("allClaims", allClaims);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred: " + e.getMessage());
        }

        return "view-claim";
    }
    @GetMapping("/view-all")
    public String viewAllClaims(Model model) {
        try {
            // Call the backend API to fetch all claims
            ResponseEntity<Claim[]> response = restTemplate.getForEntity(BASE_URL, Claim[].class);
            List<Claim> allClaims = Arrays.asList(response.getBody());

            // Add all claims to the model
            model.addAttribute("allClaims", allClaims);
        } catch (Exception e) {
            // Handle exceptions
            model.addAttribute("errorMessage", "An error occurred: " + e.getMessage());
        }

        return "view-all-claims"; // Return the view-all-claims.html page
    }
    @GetMapping("/viewClaimById")
    public String viewClaimById(@RequestParam("claimId") Long claimId, Model model) {
        try {
            String url = BASE_URL + "/" + claimId;
            ResponseEntity<Claim> response = restTemplate.getForEntity(url, Claim.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Claim claim = response.getBody();
                
                // Debugging logs
                System.out.println("Claim Data: " + claim);
                if (claim != null) {
                    System.out.println("Policy Data: " + claim.getPolicy()); // Check if policy is null
                }

                model.addAttribute("claim", claim);
            } else {
                model.addAttribute("errorMessage", "Claim not found with ID: " + claimId);
            }
        } catch (HttpClientErrorException.NotFound e) {
            model.addAttribute("errorMessage", "Claim not found with ID: " + claimId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred: " + e.getMessage());
        }

        return "view-claim";
    }

    @GetMapping("/update")
    public String showUpdateClaimPage(Model model) {
        model.addAttribute("claim", new Claim());
        return "update-claim";
    }

    @GetMapping("/view")
    public String showViewClaimsPage(Model model) {
        model.addAttribute("claim", new Claim());
        return "view-claim";
    }
}