package com.pms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pms.entity.BoughtPolicy;
import com.pms.entity.Policy;

@Service
public class PolicyClientService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${backend.url}")
    private String backendUrl; // Example: http://localhost:7281/api/policies

    public Policy createPolicy(Policy policy) {
        String url = backendUrl + "/create";
        return restTemplate.postForObject(url, policy, Policy.class);
    }

    public Policy updatePolicy(Long id, Policy policy) {
        String url = backendUrl + "/update/" + id;
        restTemplate.put(url, policy);
        return getPolicyById(id);
    }

    public Policy getPolicyById(Long id) {
        String url = backendUrl + "/" + id;
        return restTemplate.getForObject(url, Policy.class);
    }

    public Policy[] getPoliciesBySchemeName(String schemeName) {
        String url = backendUrl + "/scheme/" + schemeName;
        return restTemplate.getForObject(url, Policy[].class);
    }

    public Policy[] getPoliciesByDate(String date) {
        String url = backendUrl + "/date/" + date;
        return restTemplate.getForObject(url, Policy[].class);
    }

    public Policy[] getPoliciesByPremiumRange(Double min, Double max) {
        String url = backendUrl + "/premium?min=" + min + "&max=" + max;
        return restTemplate.getForObject(url, Policy[].class);
    }

    public Policy[] getPoliciesByMaturityRange(Double min, Double max) {
        String url = backendUrl + "/maturity?min=" + min + "&max=" + max;
        return restTemplate.getForObject(url, Policy[].class);
    }

    public Policy[] getPoliciesByYears(Integer years) {
        String url = backendUrl + "/terms/" + years;
        return restTemplate.getForObject(url, Policy[].class);
    }

    public Policy deactivatePolicy(Long id) {
        String url = backendUrl + "/deactivate/" + id;
        restTemplate.put(url, null);
        return getPolicyById(id);
    }
    
// ----- New Methods for BoughtPolicy -----
    
    public BoughtPolicy[] getBoughtPoliciesByCustomerId(String customerId) {
        String url = backendUrl + "/bought/customer/" + customerId;
        return restTemplate.getForObject(url, BoughtPolicy[].class);
    }
    
    public BoughtPolicy[] getBoughtPoliciesByScheme(String schemeName) {
        String url = backendUrl + "/bought/scheme/" + schemeName;
        return restTemplate.getForObject(url, BoughtPolicy[].class);
    }
    
    public BoughtPolicy[] getBoughtPoliciesByTerm(Integer policyTerm) {
        String url = backendUrl + "/bought/term/" + policyTerm;
        return restTemplate.getForObject(url, BoughtPolicy[].class);
    }
    
    public BoughtPolicy[] getBoughtPoliciesByMaturityRange(Double min, Double max) {
        String url = backendUrl + "/bought/maturity?min=" + min + "&max=" + max;
        return restTemplate.getForObject(url, BoughtPolicy[].class);
    }
    
    public BoughtPolicy[] getBoughtPoliciesByPremiumRange(Double min, Double max) {
        String url = backendUrl + "/bought/premium?min=" + min + "&max=" + max;
        return restTemplate.getForObject(url, BoughtPolicy[].class);
    }
}
