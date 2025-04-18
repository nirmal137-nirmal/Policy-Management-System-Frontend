package com.pms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.pms.entity.*;
import com.pms.service.PolicyClientService;

@Controller
@RequestMapping("/policy-management")
public class PolicyUIController {

    @Autowired
    private PolicyClientService policyClientService;
    
    // Dashboard page
    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard"; // returns dashboard.html
    }
    
    // ---------------------------
    // Create Policy
    // ---------------------------
    @GetMapping("/create-page")
    public String showCreatePolicyPage(Model model) {
        if (!model.containsAttribute("newPolicy")) {
            model.addAttribute("newPolicy", new Policy());
        }
        return "createPolicy"; // returns createPolicy.html
    }
    
    @PostMapping("/create")
    public String createPolicy(@ModelAttribute("newPolicy") Policy newPolicy,
                               RedirectAttributes redirectAttributes) {
        try {
            Policy createdPolicy = policyClientService.createPolicy(newPolicy);
            redirectAttributes.addFlashAttribute("message", 
                "Policy created successfully with ID: " + createdPolicy.getPolicyId());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", 
                "Error creating policy: " + ex.getMessage());
        }
        return "redirect:/policy-management/dashboard";
    }
    
    // ---------------------------
    // Update Policy
    // ---------------------------
    @GetMapping("/update-page")
    public String showUpdatePolicyPage(Model model) {
        if (!model.containsAttribute("updatePolicy")) {
            model.addAttribute("updatePolicy", new Policy());
        }
        return "updatePolicy"; // returns updatePolicy.html
    }
    
    @PostMapping("/update")
    public String updatePolicy(@RequestParam("policyIdToUpdate") Long id,
                               @ModelAttribute("updatePolicy") Policy updatePolicy,
                               RedirectAttributes redirectAttributes) {
        try {
            Policy updated = policyClientService.updatePolicy(id, updatePolicy);
            redirectAttributes.addFlashAttribute("message", 
                "Policy updated successfully for ID: " + updated.getPolicyId());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", 
                "Error updating policy: " + ex.getMessage());
        }
        return "redirect:/policy-management/dashboard";
    }
    
    // ---------------------------
    // Deactivate Policy
    // ---------------------------
    @GetMapping("/deactivate-page")
    public String showDeactivatePolicyPage() {
        return "deactivatePolicy"; // returns deactivatePolicy.html
    }
    
    @PostMapping("/deactivate")
    public String deactivatePolicy(@RequestParam("policyIdToDeactivate") Long id,
                                   RedirectAttributes redirectAttributes) {
        try {
            Policy deactivated = policyClientService.deactivatePolicy(id);
            redirectAttributes.addFlashAttribute("message", 
                "Policy deactivated successfully for ID: " + deactivated.getPolicyId());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", 
                "Error deactivating policy: " + ex.getMessage());
        }
        return "redirect:/policy-management/dashboard";
    }
    
    // ---------------------------
    // VIEW OPERATIONS DASHBOARD
    // ---------------------------
    @GetMapping("/view-dashboard")
    public String showViewDashboard() {
        return "viewDashboard"; // returns viewDashboard.html
    }
    
    // View by ID
    @GetMapping("/view-by-id")
    public String showViewByIdPage() {
        return "viewPolicyById"; // returns viewPolicyById.html
    }
    
    @PostMapping("/view-by-id")
    public String viewById(@RequestParam("policyId") Long id,
                           RedirectAttributes redirectAttributes) {
        try {
            Policy policy = policyClientService.getPolicyById(id);
            redirectAttributes.addFlashAttribute("policies", new Policy[]{policy});
        } catch(Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/policy-management/view-dashboard";
    }
    
    // View by Scheme
    @GetMapping("/view-by-scheme")
    public String showViewBySchemePage() {
        return "viewPolicyByScheme"; // returns viewPolicyByScheme.html
    }
    
    @PostMapping("/view-by-scheme")
    public String viewByScheme(@RequestParam("schemeName") String schemeName,
                               RedirectAttributes redirectAttributes) {
        try {
            Policy[] policies = policyClientService.getPoliciesBySchemeName(schemeName);
            redirectAttributes.addFlashAttribute("policies", policies);
        } catch(Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/policy-management/view-dashboard";
    }
    
    // View by Date Purchased
    @GetMapping("/view-by-date")
    public String showViewByDatePage() {
        return "viewPolicyByDate"; // returns viewPolicyByDate.html
    }
    
    @PostMapping("/view-by-date")
    public String viewByDate(@RequestParam("date") String date,
                             RedirectAttributes redirectAttributes) {
        try {
            Policy[] policies = policyClientService.getPoliciesByDate(date);
            redirectAttributes.addFlashAttribute("policies", policies);
        } catch(Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/policy-management/view-dashboard";
    }
    
    // View by Premium Range
    @GetMapping("/view-by-premium")
    public String showViewByPremiumPage() {
        return "viewPolicyByPremium"; // returns viewPolicyByPremium.html
    }
    
    @PostMapping("/view-by-premium")
    public String viewByPremium(@RequestParam("minPremium") Double min,
                                @RequestParam("maxPremium") Double max,
                                RedirectAttributes redirectAttributes) {
        try {
            Policy[] policies = policyClientService.getPoliciesByPremiumRange(min, max);
            redirectAttributes.addFlashAttribute("policies", policies);
        } catch(Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/policy-management/view-dashboard";
    }
    
    // View by Maturity Range
    @GetMapping("/view-by-maturity")
    public String showViewByMaturityPage() {
        return "viewPolicyByMaturity"; // returns viewPolicyByMaturity.html
    }
    
    @PostMapping("/view-by-maturity")
    public String viewByMaturity(@RequestParam("minMaturity") Double min,
                                 @RequestParam("maxMaturity") Double max,
                                 RedirectAttributes redirectAttributes) {
        try {
            Policy[] policies = policyClientService.getPoliciesByMaturityRange(min, max);
            redirectAttributes.addFlashAttribute("policies", policies);
        } catch(Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/policy-management/view-dashboard";
    }
    
    // View by Number of Years
    @GetMapping("/view-by-years")
    public String showViewByYearsPage() {
        return "viewPolicyByYears"; // returns viewPolicyByYears.html
    }
    
    @PostMapping("/view-by-years")
    public String viewByYears(@RequestParam("terms") Integer terms,
                              RedirectAttributes redirectAttributes) {
        try {
            Policy[] policies = policyClientService.getPoliciesByYears(terms);
            redirectAttributes.addFlashAttribute("policies", policies);
        } catch(Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/policy-management/view-dashboard";
    }
    
    // ---------------------------
    // BOUGHT POLICY DASHBOARD & SEARCH OPERATIONS
    // ---------------------------
    
    // Mapping for the new Bought Policy Dashboard
    @GetMapping("/view-bought-dashboard")
    public String showViewBoughtPolicyDashboard() {
        return "viewBoughtPolicyDashboard"; // returns viewBoughtPolicyDashboard.html
    }
    
    // Mapping for view by Customer ID for bought policies
    @GetMapping("/view-bought-by-customerId")
    public String showViewBoughtByCustomerIdPage(Model model) {
        if(!model.containsAttribute("customerId")) {
            model.addAttribute("customerId", "");
        }
        return "viewBoughtPolicyByCustomerId"; // create this template accordingly
    }
    
    @PostMapping("/view-bought-by-customerId")
    public String viewBoughtByCustomerId(@RequestParam("customerId") String customerId,
                                         RedirectAttributes redirectAttributes) {
        try {
            // Assumes a corresponding client service method exists
            BoughtPolicy[] policies = policyClientService.getBoughtPoliciesByCustomerId(customerId);
            redirectAttributes.addFlashAttribute("policies", policies);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/policy-management/view-bought-dashboard";
    }
    
    // Mapping for view by Scheme for bought policies
    @GetMapping("/view-bought-by-scheme")
    public String showViewBoughtBySchemePage(Model model) {
        if(!model.containsAttribute("schemeName")) {
            model.addAttribute("schemeName", "");
        }
        return "viewBoughtPolicyByScheme"; // create this template accordingly
    }
    
    @PostMapping("/view-bought-by-scheme")
    public String viewBoughtByScheme(@RequestParam("schemeName") String schemeName,
                                     RedirectAttributes redirectAttributes) {
        try {
            BoughtPolicy[] policies = policyClientService.getBoughtPoliciesByScheme(schemeName);
            redirectAttributes.addFlashAttribute("policies", policies);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/policy-management/view-bought-dashboard";
    }
    
    // Mapping for view by Policy Term for bought policies
    @GetMapping("/view-bought-by-term")
    public String showViewBoughtByTermPage(Model model) {
        if(!model.containsAttribute("policyTerm")) {
            model.addAttribute("policyTerm", "");
        }
        return "viewBoughtPolicyByTerm"; // create this template accordingly
    }
    
    @PostMapping("/view-bought-by-term")
    public String viewBoughtByTerm(@RequestParam("policyTerm") Integer policyTerm,
                                   RedirectAttributes redirectAttributes) {
        try {
            BoughtPolicy[] policies = policyClientService.getBoughtPoliciesByTerm(policyTerm);
            redirectAttributes.addFlashAttribute("policies", policies);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/policy-management/view-bought-dashboard";
    }
    
    // Mapping for view by Maturity Range for bought policies
    @GetMapping("/view-bought-by-maturity")
    public String showViewBoughtByMaturityPage(Model model) {
        if(!model.containsAttribute("minMaturity")) {
            model.addAttribute("minMaturity", 0.0);
        }
        if(!model.containsAttribute("maxMaturity")) {
            model.addAttribute("maxMaturity", 0.0);
        }
        return "viewBoughtPolicyByMaturity"; // create this template accordingly
    }
    
    @PostMapping("/view-bought-by-maturity")
    public String viewBoughtByMaturity(@RequestParam("minMaturity") Double minMaturity,
                                       @RequestParam("maxMaturity") Double maxMaturity,
                                       RedirectAttributes redirectAttributes) {
        try {
            BoughtPolicy[] policies = policyClientService.getBoughtPoliciesByMaturityRange(minMaturity, maxMaturity);
            redirectAttributes.addFlashAttribute("policies", policies);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/policy-management/view-bought-dashboard";
    }
    
    // Mapping for view by Premium Range for bought policies
    @GetMapping("/view-bought-by-premium")
    public String showViewBoughtByPremiumPage(Model model) {
        if(!model.containsAttribute("minPremium")) {
            model.addAttribute("minPremium", 0.0);
        }
        if(!model.containsAttribute("maxPremium")) {
            model.addAttribute("maxPremium", 0.0);
        }
        return "viewBoughtPolicyByPremium"; // create this template accordingly
    }
    
    @PostMapping("/view-bought-by-premium")
    public String viewBoughtByPremium(@RequestParam("minPremium") Double minPremium,
                                      @RequestParam("maxPremium") Double maxPremium,
                                      RedirectAttributes redirectAttributes) {
        try {
            BoughtPolicy[] policies = policyClientService.getBoughtPoliciesByPremiumRange(minPremium, maxPremium);
            redirectAttributes.addFlashAttribute("policies", policies);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/policy-management/view-bought-dashboard";
    }
}
