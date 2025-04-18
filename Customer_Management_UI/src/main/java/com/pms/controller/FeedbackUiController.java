package com.pms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.pms.entity.Feedback;
import com.pms.entity.Scheme;
import com.pms.entity.Customer;
import java.util.List;

@Controller
@RequestMapping("/feedback")
public class FeedbackUiController {
    
    private final String BASE_URL = "http://localhost:8030/api/feedback";
    
    @Autowired
    private RestTemplate restTemplate;
    @GetMapping
    public String feedbackHome() {
        return "feedbackindex";
    }

    
    @GetMapping("/submit")
    public String showSubmitForm(Model model) {
        Feedback feedback = new Feedback();
        feedback.setScheme(new Scheme());
        feedback.setCustomer(new Customer());
        model.addAttribute("feedback", feedback);
        return "submit-feedback";
    }
    
   /* @PostMapping("/submit")
    public String submitFeedback(@ModelAttribute Feedback feedback, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "submit-feedback";
        }
        try {
            ResponseEntity<?> response = restTemplate.postForEntity(BASE_URL + "/submit", feedback, Object.class);
            model.addAttribute("message", "Feedback submitted successfully!");
            return "redirect:/";
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", e.getResponseBodyAsString());
            return "submit-feedback";
        } catch (Exception e) {
            model.addAttribute("error", "Unexpected error: " + e.getMessage());
            return "submit-feedback";
        }
    }*/
    @PostMapping("/submit")
    public String submitFeedback(@ModelAttribute Feedback feedback, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "submit-feedback";
        }
        try {
            ResponseEntity<?> response = restTemplate.postForEntity(BASE_URL + "/submit", feedback, Object.class);
            
            // ✅ Clear form by resetting feedback object
            Feedback clearedFeedback = new Feedback();
            clearedFeedback.setScheme(new Scheme());
            clearedFeedback.setCustomer(new Customer());

            model.addAttribute("feedback", clearedFeedback); // add new cleared object
            model.addAttribute("message", "Feedback submitted successfully!");
            
            return "submit-feedback"; // stay on same page with cleared form
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", e.getResponseBodyAsString());
            return "submit-feedback";
        } catch (Exception e) {
            model.addAttribute("error", "Unexpected error: " + e.getMessage());
            return "submit-feedback";
        }
    }
   

    
    
    @GetMapping("/update")
    public String showUpdateForm(@RequestParam("schemeId") int schemeId, 
                                 @RequestParam("customerId") String customerId, 
                                 Model model) {
        Feedback feedback = new Feedback();
        Scheme scheme = new Scheme();
        scheme.setId(schemeId);
        Customer customer = new Customer();
        customer.setId(customerId);
        feedback.setScheme(scheme);
        feedback.setCustomer(customer);
        model.addAttribute("feedback", feedback);
        return "update-feedback";
    }
   

    
    @PostMapping("/update")
    public String updateFeedback(@ModelAttribute Feedback feedback, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "update-feedback";
        }
        try {
            ResponseEntity<?> response = restTemplate.postForEntity(BASE_URL + "/update", feedback, Object.class);
            if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                model.addAttribute("error", response.getBody());
                return "update-feedback";
            }
            model.addAttribute("message", "Feedback updated successfully!");
            int schemeId = feedback.getScheme().getId();
            String customerId = feedback.getCustomer().getId();
            return "redirect:/feedback/view?schemeId=" + schemeId + "&customerId=" + customerId;
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", e.getResponseBodyAsString());
            return "update-feedback";
        } catch (Exception e) {
            model.addAttribute("error", "Unexpected error: " + e.getMessage());
            return "update-feedback";
        }
    }
   
    
    
 
    @GetMapping("/view")
    public String viewFeedbackBySchemeAndCustomer(
            @RequestParam(required = false) Integer schemeId, 
            @RequestParam(required = false) String customerId, 
            Model model) {
        if (schemeId == null || customerId == null) {
            model.addAttribute("error", "Please enter both Scheme ID and Customer ID to check status");
            return "check-status";
        }
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(
                BASE_URL + "/view?schemeId=" + schemeId + "&customerId=" + customerId, List.class);
            List<Feedback> feedbackList = response.getBody();
            if (feedbackList == null || feedbackList.isEmpty()) {
                model.addAttribute("error", "No feedback found for Scheme ID: " + schemeId + " and Customer ID: " + customerId);
            } else {
                model.addAttribute("feedbackList", feedbackList);
            }
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", e.getResponseBodyAsString());
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching feedback: " + e.getMessage());
        }
        return "check-status";
    }
    
    @GetMapping("/admin")
    public String viewAdminFeedback(@RequestParam(required = false) Integer schemeId, 
                                    @RequestParam(required = false) String status, 
                                    Model model) {
        if (schemeId == null) {
            model.addAttribute("error", "Please enter a Scheme ID");
            return "admin-feedback";
        }
        try {
            String url = BASE_URL + "/admin?schemeId=" + schemeId + (status != null ? "&status=" + status : "");
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            List<Feedback> feedbackList = response.getBody();
            if (feedbackList == null || feedbackList.isEmpty()) {
                if ("reviewed".equals(status)) {
                    model.addAttribute("error", "No reviewed feedback found for Scheme ID: " + schemeId);
                } else if ("pending".equals(status)) {
                    model.addAttribute("error", "No pending feedback found for Scheme ID: " + schemeId);
                } else {
                    model.addAttribute("error", "No feedback found for Scheme ID: " + schemeId);
                }
            } else {
                model.addAttribute("feedbackList", feedbackList);
            }
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", e.getResponseBodyAsString());
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching feedback: " + e.getMessage());
        }
        return "admin-feedback";
    }
    
    @PostMapping("/updateStatus")
    public String updateFeedbackStatus(@RequestParam Long feedbackId, @RequestParam String status, 
                                       @RequestParam int schemeId, Model model) {
        try {
            restTemplate.postForEntity(BASE_URL + "/updateStatus?feedbackId=" + feedbackId + "&status=" + status, null, Object.class);
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", e.getResponseBodyAsString());
        } catch (Exception e) {
            model.addAttribute("error", "Error updating feedback status: " + e.getMessage());
        }
        return "redirect:/feedback/admin?schemeId=" + schemeId;
    }
}
