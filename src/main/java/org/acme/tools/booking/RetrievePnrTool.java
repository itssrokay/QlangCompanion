package org.acme.tools.booking;

import org.acme.api.RetrievePnrApi;
import org.acme.intent.RetrievePnrIntent;

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
        return String.format("""
            <h3>PNR Details Retrieved Successfully</h3>
            
            <p><b>PNR:</b> %s</p>
            <p><b>Passenger:</b> John %s</p>
            <p><b>Flight:</b> AB1234</p>
            <p><b>Route:</b> New York (JFK) → Los Angeles (LAX)</p>
            <p><b>Departure:</b> March 25, 2024 at 10:00 AM</p>
            <p><b>Arrival:</b> March 25, 2024 at 1:00 PM</p>
            <p><b>Seat:</b> 12A</p>
            <p><b>Class:</b> Economy</p>
            <p><b>Status:</b> Confirmed</p>
            """, pnr, lastName);
    }
} 