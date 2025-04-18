package com.pms.controller;

import com.pms.entity.Policy;
import com.pms.entity.BoughtPolicy;
import com.pms.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
public class BoughtPolicyUiController {

    private final String BACKEND_URL = "http://localhost:8030/policy";
    
    @Autowired
    private RestTemplate restTemplate;
    
    // GET method to load the buy policy page.
    @GetMapping("/buyPolicy")
    public String showBuyPolicyPage(@RequestParam("policyId") String policyId, Model model) {
        ResponseEntity<Policy> response = restTemplate.getForEntity(BACKEND_URL + "/" + policyId, Policy.class);
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Policy policy = response.getBody();
            model.addAttribute("policy", policy);

            // Creating and populating BoughtPolicy object
            BoughtPolicy boughtPolicy = new BoughtPolicy();
            boughtPolicy.setPolicy(policy);
            boughtPolicy.setStartDate(policy.getStartDate());
            boughtPolicy.setTotalPremiumAmount(policy.getTotalPremiumAmount());
            boughtPolicy.setMaturityAmount(policy.getMaturityAmount());
            boughtPolicy.setPolicyTerm(policy.getPolicyTerm());
            boughtPolicy.setPolicyStatus(policy.getPolicyStatus());
            boughtPolicy.setAnnuityTerm(policy.getAnnuityTerm());

            model.addAttribute("boughtPolicy", boughtPolicy);
            return "buyPolicy";
        } else {
            model.addAttribute("error", "Policy not found or error fetching policy details.");
            return "error";
        }
    }
    
    // POST method to submit the buy policy form.
    @PostMapping("/buyPolicy")
    public String buyPolicy(@ModelAttribute("boughtPolicy") BoughtPolicy boughtPolicy,
                            @RequestParam("customerId") String customerId,
                            Model model) {
        // Associate the customer with the bought policy
        Customer customer = new Customer();
        customer.setId(customerId);
        boughtPolicy.setCustomer(customer);

        // Prepare and send the request to save the bought policy
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoughtPolicy> requestEntity = new HttpEntity<>(boughtPolicy, headers);

        String url = BACKEND_URL + "/buy";
        ResponseEntity<BoughtPolicy> response = restTemplate.postForEntity(url, requestEntity, BoughtPolicy.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            model.addAttribute("boughtPolicy", response.getBody());
            return "boughtPolicyDetail";
        } else {
            model.addAttribute("error", "Failed to buy policy");
            return "error";
        }
    }

}
