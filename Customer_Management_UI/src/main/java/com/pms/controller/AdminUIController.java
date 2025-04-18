package com.pms.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pms.entity.Admin;
import com.pms.entity.Claim;
import com.pms.entity.Customer;
import com.pms.entity.Feedback;
import com.pms.entity.Payment;
import com.pms.entity.Policy;
import com.pms.entity.Scheme;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminUIController {
	private final String BASE_URL = "http://localhost:8030"; // Backend service

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ObjectMapper objectMapper;

	@GetMapping("/adminLogin")
	public String showLoginForm(Model model) {
	    model.addAttribute("admin", new Admin());
	    return "adminLogIn";
	}
	
	
	

	@PostMapping("/loginAdmin")
	public String loginAdmin(@ModelAttribute Admin adm, Model model,  HttpSession session) {
//	    ResponseEntity<String> response = restTemplate.postForEntity(
//	            BASE_URL + "/admin/login", admin, String.class);
//
//	    String result = response.getBody();
//
//	    if ("Login successful".equals(result)) {
//	        return "adminDashboard";
//	    } else {
//	    	model.addAttribute("error", result);
//	        return "adminLogIn";
//	    }
//	    
	    try {
	        ResponseEntity<Admin> response = restTemplate.postForEntity(BASE_URL + "/admin/login", adm, Admin.class);
	        Admin admin = response.getBody();

	        if (admin != null) {
	            session.setAttribute("loggedInAdmin", admin);
	            model.addAttribute("admin", admin);
	            return "adminDashboard";
	        }
	    }catch (HttpClientErrorException e) {
	        try {
	            // Extract the "error" message from response
	            String errorMessage = objectMapper.readTree(e.getResponseBodyAsString()).get("error").asText();
	            model.addAttribute("error", errorMessage);
	        } catch (Exception e1) {
	            model.addAttribute("error", "An error occurred while processing the response.");
	        }
	        return "adminLogIn";
	    }
	    return "adminLogIn";
	}
	
	@GetMapping("/adminAccount")
    public String getAccountDetails(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");

        if (admin == null) {
            return "redirect:/adminLogin";
        }

        model.addAttribute("admin", admin);
        return "adminAccount"; 
    }
	
	
	@GetMapping("/adminDashboard")
	public String dashboardPage(HttpSession session,Model model) {
		Admin admin = (Admin) session.getAttribute("loggedInAdmin");
		 model.addAttribute("admin", admin);
	    return "adminDashboard";
	}
	
	@GetMapping("/fetchVerifiedCustomers")
	public String fetchVerifiedCustomers(Model model) {
	    ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL + "/admin/viewVerifiedCustomers", List.class);
	    model.addAttribute("customers", response.getBody());
	    return "customerList";
	}

	@GetMapping("/unverifiedCustomers")
	public String unverifiedCustomers(Model model) {
	    ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL + "/admin/viewUnverifiedCustomers", List.class);
	    model.addAttribute("customers", response.getBody());
	    return "unVerifiedList";
	}
	
	@GetMapping("/deletedCustomers")
	public String deletedCustomers(Model model) {
	    ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL + "/admin/viewDeletedCustomers", List.class);
	    model.addAttribute("customers", response.getBody());
	    return "deletedList";
	}
	
	@GetMapping("/acceptCust")
	public String acceptCustomer(@RequestParam("id") String id, @RequestParam("email") String email, @RequestParam("name") String name) {
	    Customer customer = new Customer();
	    customer.setId(id);
	    customer.setEmail(email);
	    customer.setName(name);

	    ResponseEntity<String> response = restTemplate.postForEntity(
	            BASE_URL + "/admin/acceptCustomer", customer, String.class);

	    return "redirect:/unverifiedCustomers";
	}
	
	@GetMapping("/acceptCustInDeleted")
	public String acceptCustInDeleted(@RequestParam("id") String id,@RequestParam("email") String email, @RequestParam("name") String name) {
	    Customer customer = new Customer();
	    customer.setId(id);
	    customer.setEmail(email);
	    customer.setName(name);

	    ResponseEntity<String> response = restTemplate.postForEntity(
	            BASE_URL + "/admin/acceptCustomer", customer, String.class);

	    return "redirect:/deletedCustomers";
	}
	
	@GetMapping("/rejectCust")
	public String rejectCustomer(@RequestParam("id") String id, @RequestParam("email") String email, @RequestParam("name") String name) {
	    Customer customer = new Customer();
	    customer.setId(id);
	    customer.setEmail(email);
	    customer.setName(name);

	    ResponseEntity<String> response = restTemplate.postForEntity(
	            BASE_URL + "/admin/rejectCustomer", customer, String.class);

	    return "redirect:/unverifiedCustomers";
	}
	
	@GetMapping("/deleteCust")
	public String deleteCustomer(@RequestParam("id") String id, @RequestParam("email") String email, @RequestParam("name") String name) {
	    Customer customer = new Customer();
	    customer.setId(id);
	    customer.setEmail(email);
	    customer.setName(name);

	    ResponseEntity<String> response = restTemplate.postForEntity(
	            BASE_URL + "/admin/deleteCustomer", customer, String.class);

	    return "redirect:/fetchVerifiedCustomers";
	}
	
	@GetMapping("/adminHomePage")
	public String showAdminHomePage() {
	    return "adminHomePage";
	}

	@GetMapping("/searchCustomerById")
	public String searchCustomerById(@RequestParam String query, Model model) {
		try {
			ResponseEntity<Customer> response = restTemplate.getForEntity(BASE_URL + "/admin/viewCustomerById/" + query, Customer.class);
			Customer customer = response.getBody();

			if (customer != null) {
				model.addAttribute("customers", List.of(customer)); // Wrap in a list
			} else {
				model.addAttribute("error", "No customer found with ID: " + query);
			}
		} catch (HttpClientErrorException e) {
			model.addAttribute("error", "Customer not found with ID: " + query);
		}
		return "adminHomePage";
	}

	@GetMapping("/searchCustomerByName")
	public String searchCustomerByName(@RequestParam String query, Model model) {
		try {
			ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL + "/admin/viewCustomerByName/" + query, List.class);
			List<Customer> customers = response.getBody();

			if (customers != null && !customers.isEmpty()) {
				model.addAttribute("customers", customers);
			} else {
				model.addAttribute("error", "No customers found with name: " + query);
			}
		} catch (HttpClientErrorException e) {
			model.addAttribute("error", "Error searching for customer: " + e.getMessage());
		}
		return "adminHomePage";
	}
	@GetMapping("/policyDashboard")
	public String showPolicyDashboard(Model model) {
	    ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL + "/policy/viewPolicies", List.class);
	    model.addAttribute("policies", response.getBody());
	    return "policyDashboard";
	}

	@GetMapping("/viewPolicyDetailsInAdmin")

	public String showPolicyDetailsInAdmin(@RequestParam("policyId") String policyId, Model model) {

	Policy p = new Policy();

	p.setPolicyId(policyId);

	ResponseEntity<Policy> response = restTemplate.postForEntity(BASE_URL + "/policy/viewPolicyDetails", p, Policy.class);

	model.addAttribute("policy", response.getBody());

	return "policyPageInAdmin";

	}
	
	@GetMapping("/viewSchemesInAdmin")
	public String viewSchemesInAdmin(Model model) {
		ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL + "/api/schemes/viewSchemes", List.class);
	    model.addAttribute("schemes", response.getBody());
	    return "schemeDashboard";
	}
	@GetMapping("/viewAdminPoliciesInScheme")
	public String viewAdminPoliciesInScheme(Model model, @RequestParam("schemeId") int schemeId) {
		Scheme s=new Scheme();
		s.setId(schemeId);
		ResponseEntity<List> response = restTemplate.postForEntity(BASE_URL + "/policy/viewSchemePolicies", s , List.class);
	    model.addAttribute("policies", response.getBody());
	    return "policyListInAdmin";
	}
	@GetMapping("/viewCustomer")
	public String viewCustomer(@RequestParam("id") String id, Model model) {
	    Customer customer = new Customer();
	    customer.setId(id);

	    ResponseEntity<Object> response = restTemplate.postForEntity(BASE_URL + "/admin/viewCustomerDetailsWithPoliciesAndPayments", customer, Object.class);

	    Map<String, Object> map = objectMapper.convertValue(response.getBody(), Map.class);

	    Customer customerDetails = objectMapper.convertValue(map.get("customerDetails"), Customer.class);
	    List<Policy> policyList = objectMapper.convertValue(map.get("policyList"), objectMapper.getTypeFactory().constructCollectionType(List.class, Policy.class));
	    List<Payment> paymentList = objectMapper.convertValue(map.get("paymentList"), objectMapper.getTypeFactory().constructCollectionType(List.class, Payment.class));

	    model.addAttribute("customer", customerDetails);
	    model.addAttribute("policies", policyList);
	    model.addAttribute("payments", paymentList);

	    return "view-customer";
	}
	
	@GetMapping("/index")
    public String showHomePage(Model model) {
        Claim claim = new Claim();
        claim.setPolicy(new Policy()); 
        model.addAttribute("claim", claim);
        return "index";
    }

	@GetMapping("/admin")
    public String viewAdminFeedback(@RequestParam(required = false) Integer schemeId, 
                                    @RequestParam(required = false) String status, 
                                    Model model) {
        return "admin-feedback";
    }

	
}
