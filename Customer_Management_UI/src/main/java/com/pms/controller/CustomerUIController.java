package com.pms.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pms.entity.Customer;
import com.pms.entity.Policy;
import com.pms.entity.Scheme;
import com.pms.entity.BoughtPolicy;

@Controller
public class CustomerUIController {

	private final String BASE_URL = "http://localhost:8030"; // Backend service

	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/")
	public String home() {
		return "home";
	}
	@GetMapping("/contact")
	public String contact() {
		return "contact";
	}
	@GetMapping("/custHomeDashboard")
	public String custHomeDashboard(HttpSession session, Model model) {
	    // Log session attributes
	    System.out.println("Session ID: " + session.getId());
	    System.out.println("LoggedInCustomer in session: " + session.getAttribute("loggedInCustomer"));

	    Customer customer = (Customer) session.getAttribute("loggedInCustomer");

	    if (customer == null) {
	        System.out.println("Customer is null, redirecting to login.");
	        return "redirect:/login";
	    }

	    model.addAttribute("customer", customer);
	    return "custHomeDashboard";
	}

	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("customer", new Customer());
		return "register";
	}

	@Autowired
	private ObjectMapper objectMapper; // Inject ObjectMapper

//	@PostMapping("/addCustomer")
//	public String registerCustomer(@Valid @ModelAttribute Customer cust, BindingResult result, Model model) {
//	    if (result.hasErrors()) {
//	        return "register";
//	    }
//
//	    try {
//	        ResponseEntity<String> response = restTemplate.postForEntity(
//	                BASE_URL + "/customer/register", cust, String.class);
//	        String responseBody = response.getBody();
//	        if ("Customer already exists".equals(responseBody)) {
//	            model.addAttribute("error", "Customer already exists. Please try logging in.");
//	            return "register";
//	        }
//	        if ("Phone number cannot exceed 10 digits".equals(responseBody)) {
//	            model.addAttribute("error", "Phone number cannot exceed 10 digits");
//	            return "register";
//	        }
//	        model.addAttribute("customerId", response.getBody());
//	        return "message";
//	    } catch (HttpClientErrorException.BadRequest e) {
//	        try {
//	            // Parse JSON response if available
//	            Map<String, String> errors = objectMapper.readValue(e.getResponseBodyAsString(), Map.class);
//	            model.addAttribute("validationErrors", errors);
//	        } catch (JsonProcessingException ex) {
//	            model.addAttribute("error", "An error occurred while processing the response.");
//	        }
//	        return "register";
//	    } catch (Exception e) {
//	        model.addAttribute("error", "An unexpected error occurred. Please try again later.");
//	        return "register";
//	    }
//	}

	
	@PostMapping("/addCustomer")
	public String registerCustomer(@ModelAttribute("customer") @Validated Customer cust, BindingResult result, Model model) {
	    try {
	        ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL + "/customer/register", cust, String.class);
	        String responseBody = response.getBody();

	        if ("Customer already exists".equals(responseBody)) {
	            model.addAttribute("error", "Customer already exists. Please try logging in.");
	            return "register";
	        }

	        model.addAttribute("customerId", response.getBody());
	        return "message"; // Success Page

	    } catch (HttpClientErrorException e) {
	    	 try {
	             Map<String, String> errors = objectMapper.readValue(
	                 e.getResponseBodyAsString(), new TypeReference<Map<String, String>>() {});

	             if (errors != null && !errors.isEmpty()) {
	                 model.addAttribute("validationErrors", errors); // Store errors in model
	             }
	         } catch (JsonProcessingException e1) {
	             model.addAttribute("error", "An error occurred while processing the response.");
	         }

	         return "register";
	    }
	}



	
	@GetMapping("/login")
	public String showLoginForm(Model model) {
	    model.addAttribute("customer", new Customer());
	    return "login";
	}
	
//	@PostMapping("/loginCust")
//	public String loginCustomer(@ModelAttribute Customer cust, Model model) {
//	    ResponseEntity<String> response = restTemplate.postForEntity(
//	            BASE_URL + "/customer/login", cust, String.class);
//
//	    String result = response.getBody();
//
//	    if ("Login successful".equals(result)) {
//	        return "dashboard";
//	    } else {
//	        model.addAttribute("error", result);
//	        return "login";
//	    }
//	}
	
//	@PostMapping("/loginCust")
//	public String loginCustomer(@ModelAttribute Customer cust, Model model) {
//	    try {
//	        ResponseEntity<Customer> response = restTemplate.postForEntity(BASE_URL + "/customer/login",cust, Customer.class);
//	        Customer customer = response.getBody();
//
//	        if (customer != null) {
//	            model.addAttribute("customer", customer);
//	            return "dashboard"; // Redirect to dashboard
//	        }
//	    } catch (HttpClientErrorException e) {
//	        model.addAttribute("error", e.getResponseBodyAsString());
//	        return "login";
//	    }
//
//	    model.addAttribute("error", "Invalid credentials or account issue");
//	    return "login";
//	}
	
	@PostMapping("/loginCust")
	public String loginCustomer(@ModelAttribute Customer cust, Model model, HttpSession session) {
	    try {
	        ResponseEntity<Customer> response = restTemplate.postForEntity(BASE_URL + "/customer/login", cust, Customer.class);
	        Customer customer = response.getBody();

	        if (customer != null) {
	        	session.setAttribute("loggedInCustomer", customer);
	        	System.out.println("Customer stored in session: " + customer);
	            model.addAttribute("customer", customer);
	            return "dashboard";
	        }
	    }catch (HttpClientErrorException e) {
	        try {
	            // Extract the "error" message from response
	            String errorMessage = objectMapper.readTree(e.getResponseBodyAsString()).get("error").asText();
	            model.addAttribute("error", errorMessage);
	        } catch (Exception e1) {
	            model.addAttribute("error", "An error occurred while processing the response.");
	        }
	        return "login";
	    }
	    return "login";
	}
	
	@GetMapping("/custAccount")
    public String getAccountDetails(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");

        if (customer == null) {
            return "redirect:/login";
        }

        model.addAttribute("customer", customer);
        return "custAccount"; 
    }
	@GetMapping("/dashboard")
	public String showDashboardPage(HttpSession session, Model model) {
		Customer customer = (Customer) session.getAttribute("loggedInCustomer");
	    model.addAttribute("customer", customer);
	    return "dashboard";
	}
	
	@GetMapping("/updateCust")
	public String showUpdateForm(Model model) {
	    model.addAttribute("customer", new Customer());
	    return "updatePage";
	}
	
	@PostMapping("/updateCustomer")
	public String updateCustomer(@ModelAttribute Customer cust, Model model, HttpSession session) {
		

		Customer customer = (Customer) session.getAttribute("loggedInCustomer");

		cust.setId(customer.getId());
	    ResponseEntity<Customer> response = restTemplate.postForEntity(
	            BASE_URL + "/customer/updateCustomer", cust, Customer.class);
	    
	    session.setAttribute("loggedInCustomer", response.getBody());

	    return "redirect:/custAccount";
	}
	
	
	
	@GetMapping("/changePassword")
	public String changePasswordForm(Model model) {
	    model.addAttribute("customer", new Customer());
	    return "ChangePasswordPage";
	}
	
	@PostMapping("/changeCustPassword")
	public String changeCustPassword(@ModelAttribute Customer cust, Model model, HttpSession session) {
		
		Customer customer = (Customer) session.getAttribute("loggedInCustomer");

		cust.setId(customer.getId());
	    ResponseEntity<Customer> response = restTemplate.postForEntity(
	            BASE_URL + "/customer/changePassword", cust, Customer.class);

	    return "redirect:/custAccount";
	}
	
	
	
	@GetMapping("/deleteCustomer")
	public String deleteCustomer(@RequestParam("id") String id, @RequestParam("email") String email, @RequestParam("name") String name) {
	    Customer customer = new Customer();
	    customer.setId(id);
	    customer.setEmail(email);
	    customer.setName(name);

	    ResponseEntity<String> response = restTemplate.postForEntity(
	            BASE_URL + "/customer/deleteCustomer", customer, String.class);

	    return "redirect:/login";
	}
	@GetMapping("/homePage")
	public String showHomePage() {
	    return "homeMainPage";
	}
	
	@GetMapping("/homeCustPage")
	public String showCustHomePage(HttpSession session, Model model) {
		Customer customer = (Customer) session.getAttribute("loggedInCustomer");
		   model.addAttribute("customer", customer);
	    return "custHomeDashboard";
	}

//	@GetMapping("/contact")
//	public String contact() {
//		return "contact";
//	}
//	
	@GetMapping("/viewPolicies")
	public String viewPolicies(Model model) {
	    ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL + "/policy/viewPolicies", List.class);
	    model.addAttribute("policies", response.getBody());
	    return "policyList";
	}
	
	@GetMapping("/viewPolicyDetails")
	public String showPolicyDetails(@RequestParam("policyId") String policyId, Model model) {
		  Policy p = new Policy();
		  p.setPolicyId(policyId);
	    ResponseEntity<Policy> response = restTemplate.postForEntity(BASE_URL + "/policy/viewPolicyDetails", p, Policy.class);
	    model.addAttribute("policy", response.getBody());
	    return "policyPage";
	}
	
	
	
	
	
	@GetMapping("/viewPoliciesInCustDashboard")
	public String showPoliciesInCustDashboard(Model model) {
	    ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL + "/policy/viewPolicies", List.class);
	    model.addAttribute("policies", response.getBody());
	    return "policyListInCustDashboard";
	}
	
	@GetMapping("/viewPolicyDetailsInCustDashboard")
	public String showPolicyDetailsInCustDashboard(@RequestParam("policyId") String policyId, Model model) {
		  Policy p = new Policy();
		  p.setPolicyId(policyId);
	    ResponseEntity<Policy> response = restTemplate.postForEntity(BASE_URL + "/policy/viewPolicyDetails", p, Policy.class);
	    model.addAttribute("policy", response.getBody());
	    return "policyPageInCustDashboard";
	}
	
	
	@GetMapping("/viewSchemes")
	public String viewSchemes(Model model) {
		ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL + "/scheme/viewSchemes", List.class);
	    model.addAttribute("schemes", response.getBody());
	    return "schemesList";
	}
	@GetMapping("/viewPoliciesInScheme")
	public String showPoliciesInScheme(Model model, @RequestParam("schemeId") int schemeId) {
		Scheme s=new Scheme();
		s.setId(schemeId);
		ResponseEntity<List> response = restTemplate.postForEntity(BASE_URL + "/policy/viewSchemePolicies", s , List.class);
	    model.addAttribute("policies", response.getBody());
	    return "policyList";
	}
	
	@GetMapping("/custPolicies")
	public String custPolicies(Model model, HttpSession session) {
	    Customer customer = (Customer) session.getAttribute("loggedInCustomer");

	    // Set the request headers for JSON
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    // Wrap customer object in HttpEntity with headers
	    HttpEntity<Customer> request = new HttpEntity<>(customer, headers);

	    // Make the POST request with JSON headers,
	    // NOTE: Changed ParameterizedTypeReference to List<BoughtPolicy>
	    ResponseEntity<List<BoughtPolicy>> response = restTemplate.exchange(
	        BASE_URL + "/policy/viewCustPolicies",
	        HttpMethod.POST,
	        request,
	        new ParameterizedTypeReference<List<BoughtPolicy>>() {}
	    );

	    model.addAttribute("policies", response.getBody());
	    return "custPolicyList";
	}


	
	@GetMapping("/viewCustPolicyDetails")
	public String showCustPolicyDetails(@RequestParam("policyId") Long policyId, Model model) {
	    // Prepare a BoughtPolicy instance with the policy identifier (or adjust based on your logic)
	    BoughtPolicy bp = new BoughtPolicy();
	    bp.setBoughtPolicyId(policyId);
	    
	    // Call the backend endpoint that returns a BoughtPolicy.
	    ResponseEntity<BoughtPolicy> response = restTemplate.getForEntity(
	        BASE_URL + "/policy/bought/" + policyId,
	        BoughtPolicy.class
	    );
	    
	    // Add the object to the model using the name "boughtPolicy" that matches the template
	    model.addAttribute("boughtPolicy", response.getBody());
	    return "policyPageInCust";
	}

	
	@GetMapping("/viewSchemesInCustDashboard")
	public String viewSchemesInCustDashboard(Model model) {
		ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL + "/api/schemes/viewSchemes", List.class);
	    model.addAttribute("schemes", response.getBody());
	   //new change
		return "view-active-schemes";
	}
	 
	

	@GetMapping("/viewCustPoliciesInScheme")
	public String showCustPoliciesInScheme(Model model, @RequestParam("schemeId") int schemeId) {
		Scheme s=new Scheme();
		s.setId(schemeId);
		ResponseEntity<List> response = restTemplate.postForEntity(BASE_URL + "/policy/viewSchemePolicies", s , List.class);
	    model.addAttribute("policies", response.getBody());
	    return "policyListInCustDashboard";
	}
	
	
	@GetMapping("/viewCustTransactions")
    public String showCustTransactions(HttpSession session, Model model) {
        Customer cust = (Customer) session.getAttribute("loggedInCustomer");

        ResponseEntity<List> response = restTemplate.postForEntity(BASE_URL + "/payment/viewCustPayments", cust  , List.class);
	    model.addAttribute("payments", response.getBody());
	    return "custPayments";
    }
	
}
