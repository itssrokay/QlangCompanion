package org.acme.tools.booking;

import org.acme.api.RetrievePnrApi;
import org.acme.intent.RetrievePnrIntent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RetrievePnrTool {

    @Inject
    RetrievePnrIntent retrievePnrIntent;
    
    @Inject
    RetrievePnrApi retrievePnrApi;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Tool("Retrieve PNR information using PNR and last name. This tool will provide complete booking details including flight information, passenger details, and booking status.")
    public String retrievePnr(@P("The PNR (record locator) for the booking") String pnr, 
                             @P("The passenger's last name") String lastName) {
        
        System.out.println("=== PNR TOOL CALLED ===");
        System.out.println("Tool: retrievePnr");
        System.out.println("LLM Extracted Parameters:");
        System.out.println("  - pnr: " + pnr);
        System.out.println("  - lastName: " + lastName);
        
        // Step 1: Call intent function to format parameters
        System.out.println("\n--- STEP 1: CALLING INTENT FUNCTION ---");
        String intentPayload = retrievePnrIntent.retrievePnr(pnr, lastName);
        
        // Step 2: Call API handler with formatted payload
        System.out.println("\n--- STEP 2: CALLING API HANDLER ---");
        String apiResponse = retrievePnrApi.retrievePnrDapi(intentPayload);
        
        // Step 3: Format response for LLM consumption
        System.out.println("\n--- STEP 3: FORMATTING RESPONSE FOR LLM ---");
        String formattedResponse = formatPnrResponseForLlm(apiResponse, pnr, lastName);
        
        System.out.println("Final Response to LLM:");
        System.out.println(formattedResponse);
        System.out.println("=== END PNR TOOL ===");
        
        return formattedResponse;
    }
    
    private String formatPnrResponseForLlm(String apiResponse, String pnr, String lastName) {
        System.out.println("=== FORMATTING API RESPONSE FOR LLM ===");
        System.out.println("Raw API Response: " + apiResponse);
        
        try {
            // Parse the API response
            JsonNode responseNode = objectMapper.readTree(apiResponse);
            
            // Check if API call was successful
            if (responseNode.has("status") && "error".equals(responseNode.get("status").asText())) {
                // API call failed - return error message
                String errorMessage = responseNode.has("message") ? 
                    responseNode.get("message").asText() : "Unknown error occurred";
                
                return String.format("""
                    <h3>PNR Retrieval Failed</h3>
                    <p><b>Error:</b> %s</p>
                    <p><b>PNR:</b> %s</p>
                    <p><b>Last Name:</b> %s</p>
                    <p>Please check your PNR and last name, or contact customer service for assistance.</p>
                    """, errorMessage, pnr, lastName);
            }
            
            // API call was successful - parse and format the actual response
            if (responseNode.has("data")) {
                JsonNode dataNode = responseNode.get("data");
                
                // Extract passenger information
                String passengerName = extractField(dataNode, "passengerName", "passenger.name", "name");
                String flightNumber = extractField(dataNode, "flightNumber", "flight.number", "flightNo");
                String departure = extractField(dataNode, "departure", "origin", "from");
                String arrival = extractField(dataNode, "arrival", "destination", "to");
                String departureTime = extractField(dataNode, "departureTime", "departure.dateTime", "departTime");
                String arrivalTime = extractField(dataNode, "arrivalTime", "arrival.dateTime", "arrTime");
                String seatNumber = extractField(dataNode, "seatNumber", "seat", "seatNo");
                String bookingClass = extractField(dataNode, "class", "bookingClass", "travelClass");
                String status = extractField(dataNode, "status", "bookingStatus", "pnrStatus");
                
                return String.format("""
                    <h3>PNR Details Retrieved Successfully</h3>
                    
                    <p><b>PNR:</b> %s</p>
                    <p><b>Passenger:</b> %s</p>
                    <p><b>Flight:</b> %s</p>
                    <p><b>Route:</b> %s → %s</p>
                    <p><b>Departure:</b> %s</p>
                    <p><b>Arrival:</b> %s</p>
                    <p><b>Seat:</b> %s</p>
                    <p><b>Class:</b> %s</p>
                    <p><b>Status:</b> %s</p>
                    """, 
                    pnr,
                    passengerName != null ? passengerName : lastName,
                    flightNumber != null ? flightNumber : "N/A",
                    departure != null ? departure : "N/A",
                    arrival != null ? arrival : "N/A",
                    departureTime != null ? departureTime : "N/A",
                    arrivalTime != null ? arrivalTime : "N/A",
                    seatNumber != null ? seatNumber : "N/A",
                    bookingClass != null ? bookingClass : "N/A",
                    status != null ? status : "N/A"
                );
            } else {
                // No data field - return raw response in a readable format
                return String.format("""
                    <h3>PNR Information Retrieved</h3>
                    
                    <p><b>PNR:</b> %s</p>
                    <p><b>Last Name:</b> %s</p>
                    <p><b>Response:</b></p>
                    <pre>%s</pre>
                    """, pnr, lastName, apiResponse);
            }
            
        } catch (Exception e) {
            System.out.println("Error parsing API response: " + e.getMessage());
            
            // If we can't parse the API response, return it as-is in a readable format
            return String.format("""
                <h3>PNR Information Retrieved</h3>
                
                <p><b>PNR:</b> %s</p>
                <p><b>Last Name:</b> %s</p>
                <p><b>Raw Response:</b></p>
                <pre>%s</pre>
                <p><i>Note: Response format may vary from standard format.</i></p>
                """, pnr, lastName, apiResponse);
        }
    }
    
    private String extractField(JsonNode dataNode, String... fieldNames) {
        for (String fieldName : fieldNames) {
            if (fieldName.contains(".")) {
                // Handle nested fields like "passenger.name"
                String[] parts = fieldName.split("\\.");
                JsonNode current = dataNode;
                for (String part : parts) {
                    if (current != null && current.has(part)) {
                        current = current.get(part);
                    } else {
                        current = null;
                        break;
                    }
                }
                if (current != null && !current.isNull()) {
                    return current.asText();
                }
            } else {
                // Handle direct fields
                if (dataNode.has(fieldName) && !dataNode.get(fieldName).isNull()) {
                    return dataNode.get(fieldName).asText();
                }
            }
        }
        return null;
    }
} 