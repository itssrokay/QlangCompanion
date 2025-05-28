package org.acme.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RetrieveServicesApi {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String retrieveServicesDapi(String payload) {
        System.out.println("=== RETRIEVE_SERVICES API HANDLER CALLED ===");
        System.out.println("API Handler: retrieveServicesDapi");
        System.out.println("Received Payload:");
        System.out.println(payload);
        
        try {
            // Parse the payload to extract parameters
            JsonNode payloadNode = objectMapper.readTree(payload);
            String pnr = payloadNode.get("pnr").asText();
            
            System.out.println("Extracted Parameters:");
            System.out.println("  - pnr: " + pnr);
            
            // Mock API response simulating services catalog
            String mockResponse = String.format("""
                {
                    "status": "success",
                    "data": [
                        {
                            "id": "SVC001",
                            "name": "Extra Baggage",
                            "description": "Additional 23kg baggage allowance",
                            "price": {
                                "amount": 50.00,
                                "currency": "USD"
                            },
                            "flightNumber": "AB1234",
                            "category": "baggage"
                        },
                        {
                            "id": "SVC002", 
                            "name": "Seat Selection",
                            "description": "Premium seat selection with extra legroom",
                            "price": {
                                "amount": 35.00,
                                "currency": "USD"
                            },
                            "flightNumber": "AB1234",
                            "category": "seating"
                        },
                        {
                            "id": "SVC003",
                            "name": "In-flight Meal",
                            "description": "Special dietary meal selection",
                            "price": {
                                "amount": 25.00,
                                "currency": "USD"
                            },
                            "flightNumber": "AB1234",
                            "category": "meal"
                        },
                        {
                            "id": "SVC004",
                            "name": "Priority Boarding",
                            "description": "Board the aircraft before general boarding",
                            "price": {
                                "amount": 15.00,
                                "currency": "USD"
                            },
                            "flightNumber": "AB1234",
                            "category": "boarding"
                        }
                    ],
                    "pnr": "%s",
                    "totalServices": 4
                }
                """, pnr);
            
            System.out.println("Mock API Response:");
            System.out.println(mockResponse);
            System.out.println("=== END RETRIEVE_SERVICES API HANDLER ===");
            
            return mockResponse;
            
        } catch (Exception e) {
            System.out.println("Error parsing payload: " + e.getMessage());
            return "{\"status\": \"error\", \"message\": \"Failed to retrieve services\"}";
        }
    }
} 