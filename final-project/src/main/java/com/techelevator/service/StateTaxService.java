package com.techelevator.service;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StateTaxService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiUrl = "https://teapi.netlify.app/api/statetax?state=";

    public BigDecimal getTaxRate(String stateCode) {
        String url = apiUrl + stateCode;
        try {
            StateTaxResponse response = restTemplate.getForObject(url, StateTaxResponse.class);
            if (response != null && response.getSalesTax() != null) {
                // Convert percentage to decimal format
                return response.getSalesTax().divide(BigDecimal.valueOf(100));
            } else {
                return BigDecimal.ZERO;
            }
        } catch (Exception e) {
            // Log the error and return a default tax rate
            System.err.println("Error fetching tax rate for state code: " + stateCode + ". Error: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    public static class StateTaxResponse {
        private String state;
        private BigDecimal salesTax;

        // Getter and setter for state
        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        // Getter and setter for salesTax
        public BigDecimal getSalesTax() {
            return salesTax;
        }

        public void setSalesTax(BigDecimal salesTax) {
            this.salesTax = salesTax;
        }
    }
}
