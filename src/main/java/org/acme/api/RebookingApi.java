package org.acme.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RebookingApi {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String rebookingDapi(String payload) {
        System.out.println("=== REBOOKING API HANDLER CALLED ===");
        System.out.println("API Handler: rebookingDapi");
        System.out.println("Received Payload:");
        System.out.println(payload);
        
        try {
            // Parse the payload to extract parameters
            JsonNode payloadNode = objectMapper.readTree(payload);
            String pnr = payloadNode.get("pnr").asText();
            String lastName = payloadNode.get("lastName").asText();
            String newDepartureDate = payloadNode.get("newDepartureDate").asText();
            String newFlightNumber = payloadNode.get("newFlightNumber").asText();
            
            System.out.println("Extracted Parameters:");
            System.out.println("  - pnr: " + pnr);
            System.out.println("  - lastName: " + lastName);
            System.out.println("  - newDepartureDate: " + newDepartureDate);
            System.out.println("  - newFlightNumber: " + newFlightNumber);
            
            // Mock API response simulating rebooking success
            String mockResponse = String.format("""
                {
                    "status": "success",
                    "data": {
                        "originalPnr": "%s",
                        "newPnr": "%s-NEW",
                        "passengerName": "John %s",
                        "originalFlight": {
                            "flightNumber": "AB1234",
                            "departureDate": "2024-03-25",
                            "status": "Cancelled"
                        },
                        "newFlight": {
                            "flightNumber": "%s",
                            "departureDate": "%s",
                            "departure": {
                                "airport": "JFK",
                                "city": "New York",
                                "dateTime": "%sT14:30:00"
                            },
                            "arrival": {
                                "airport": "LAX",
                                "city": "Los Angeles", 
                                "dateTime": "%sT17:45:00"
                            },
                            "status": "Confirmed"
                        },
                        "rebookingFee": {
                            "amount": 75.00,
                            "currency": "USD"
                        },
                        "seatNumber": "15C"
                    }
                }
                """, pnr, pnr, lastName, newFlightNumber, newDepartureDate, newDepartureDate, newDepartureDate);
            
            System.out.println("Mock API Response:");
            System.out.println(mockResponse);
            System.out.println("=== END REBOOKING API HANDLER ===");
            
            return mockResponse;
            
        } catch (Exception e) {
            System.out.println("Error parsing payload: " + e.getMessage());
            return "{\"status\": \"error\", \"message\": \"Failed to process rebooking request\"}";
        }
    }
}