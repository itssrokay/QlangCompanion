package org.acme.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RefundApi {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String refundDapi(String payload) {
        System.out.println("=== REFUND API HANDLER CALLED ===");
        System.out.println("API Handler: refundDapi");
        System.out.println("Received Payload:");
        System.out.println(payload);
        
        try {
            // Parse the payload to extract parameters
            JsonNode payloadNode = objectMapper.readTree(payload);
            String pnr = payloadNode.get("pnr").asText();
            String lastName = payloadNode.get("lastName").asText();
            String targetAction = payloadNode.get("targetAction").asText();
            
            System.out.println("Extracted Parameters:");
            System.out.println("  - pnr: " + pnr);
            System.out.println("  - lastName: " + lastName);
            System.out.println("  - targetAction: " + targetAction);
            
            // Mock API response simulating refund eligibility check
            String mockResponse = String.format("""
                {
                    "status": "success",
                    "data": {
                        "pnr": "%s",
                        "passengerName": "John %s",
                        "refundEligible": true,
                        "refundAmount": {
                            "amount": 450.00,
                            "currency": "USD"
                        },
                        "refundPolicy": "Full refund available - cancellation within 24 hours",
                        "processingTime": "5-7 business days",
                        "refundMethod": "Original payment method",
                        "cancellationFee": {
                            "amount": 0.00,
                            "currency": "USD"
                        }
                    }
                }
                """, pnr, lastName);
            
            System.out.println("Mock API Response:");
            System.out.println(mockResponse);
            System.out.println("=== END REFUND API HANDLER ===");
            
            return mockResponse;
            
        } catch (Exception e) {
            System.out.println("Error parsing payload: " + e.getMessage());
            return "{\"status\": \"error\", \"message\": \"Failed to process refund request\"}";
        }
    }
} 