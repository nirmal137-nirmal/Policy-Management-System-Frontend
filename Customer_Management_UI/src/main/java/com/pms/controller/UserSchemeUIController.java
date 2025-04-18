package com.pms.controller;

import com.pms.entity.Scheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/user-schemes")
public class UserSchemeUIController {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public UserSchemeUIController(RestTemplate restTemplate,
                                  @Value("${backend.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @GetMapping
    public String defaultPage(Model model) {
        return "redirect:/user-schemes/active";  // Redirects to active schemes
    }

    @GetMapping("/view-more")
    public String getSchemeByID(@RequestParam(name = "id", required = false) Long id, Model model) {
        if (id == null) {
            model.addAttribute("errorMessage", "Please enter a valid Scheme ID.");
            return "apply-scheme";
        }
        try {
            Scheme scheme = restTemplate.getForObject(baseUrl + "/{id}", Scheme.class, id);
            model.addAttribute("scheme", scheme);  // Use singular attribute name
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Scheme not found.");
        }
        return "apply-scheme";
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
        return "view-active-schemes";
    }



//    @GetMapping("/apply")
//    public String applyToScheme(@RequestParam int schemeId, @RequestParam int userId, Model model) {
//        try {
//            UserScheme appliedScheme = restTemplate.postForObject(baseUrl + "/apply?schemeId=" + schemeId + "&userId=" + userId, null, UserScheme.class);
//            model.addAttribute("successMessage", "Application submitted successfully.");
//        } catch (Exception e) {
//            model.addAttribute("errorMessage", "Failed to apply for scheme: " + e.getMessage());
//        }
//        return "redirect:/user-schemes/user/" + userId;
//    }

//    @GetMapping("/user/{userId}")
//    public String viewUserSchemes(@PathVariable int userId, Model model) {
//        try {
//            UserScheme[] schemesArray = restTemplate.getForObject(baseUrl + "/user/" + userId, UserScheme[].class);
//            List<UserScheme> schemes = Arrays.asList(schemesArray);
//            model.addAttribute("schemes", schemes);
//        } catch (Exception e) {
//            model.addAttribute("errorMessage", "Failed to load user schemes: " + e.getMessage());
//        }
//        return "user-scheme-list";
//    }

    @GetMapping("/active")
    public String viewActiveSchemes(Model model) {
        try {
            Scheme[] schemesArray = restTemplate.getForObject(baseUrl + "/active", Scheme[].class);
            List<Scheme> schemes = Arrays.asList(schemesArray);
            model.addAttribute("schemes", schemes);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to load active schemes: " + e.getMessage());
        }
        return "view-active-schemes";
    }

//    @GetMapping("/status")
//    public String getApplicationStatus(@RequestParam int userId, @RequestParam int schemeId, Model model) {
//        try {
//            String status = restTemplate.getForObject(baseUrl + "/status?userId=" + userId + "&schemeId=" + schemeId, String.class);
//            model.addAttribute("statusMessage", "Application status: " + status);
//        } catch (Exception e) {
//            model.addAttribute("errorMessage", "Failed to retrieve status: " + e.getMessage());
//        }
//        return "application-status";
//    }
}
