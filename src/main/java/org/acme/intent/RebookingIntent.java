package org.acme.intent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RebookingIntent {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String rebooking(String pnr, String lastName, String newDepartureDate, String newFlightNumber) {
        System.out.println("=== REBOOKING INTENT FUNCTION CALLED ===");
        System.out.println("Function: rebooking");
        System.out.println("Input Parameters:");
        System.out.println("  - pnr: " + pnr);
        System.out.println("  - lastName: " + lastName);
        System.out.println("  - newDepartureDate: " + newDepartureDate);
        System.out.println("  - newFlightNumber: " + newFlightNumber);
        
        // Create JSON payload
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("pnr", pnr);
        payload.put("lastName", lastName);
        payload.put("newDepartureDate", newDepartureDate);
        payload.put("newFlightNumber", newFlightNumber);
        payload.put("action", "rebooking");
        payload.put("timestamp", System.currentTimeMillis());
        
        String jsonPayload = payload.toString();
        
        System.out.println("Formatted Payload:");
        System.out.println(jsonPayload);
        System.out.println("=== END REBOOKING INTENT ===");
        
        return jsonPayload;
    }
} 