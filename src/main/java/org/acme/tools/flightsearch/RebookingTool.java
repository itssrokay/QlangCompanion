package org.acme.tools.flightsearch;

import org.acme.api.RebookingApi;
import org.acme.intent.RebookingIntent;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RebookingTool {

    @Inject
    RebookingIntent rebookingIntent;
    
    @Inject
    RebookingApi rebookingApi;

    @Tool("Rebook a flight to a new date and flight number using PNR and last name. This tool will change the existing booking to new flight details.")
    public String rebookFlight(@P("The PNR (record locator) for the booking to rebook") String pnr, 
                              @P("The passenger's last name") String lastName,
                              @P("The new departure date in YYYY-MM-DD format") String newDepartureDate,
                              @P("The new flight number") String newFlightNumber) {
        
        System.out.println("=== REBOOKING TOOL CALLED ===");
        System.out.println("Tool: rebookFlight");
        System.out.println("LLM Extracted Parameters:");
        System.out.println("  - pnr: " + pnr);
        System.out.println("  - lastName: " + lastName);
        System.out.println("  - newDepartureDate: " + newDepartureDate);
        System.out.println("  - newFlightNumber: " + newFlightNumber);
        
        // Step 1: Call intent function to format parameters
        System.out.println("\n--- STEP 1: CALLING INTENT FUNCTION ---");
        String intentPayload = rebookingIntent.rebooking(pnr, lastName, newDepartureDate, newFlightNumber);
        
        // Step 2: Call API handler with formatted payload
        System.out.println("\n--- STEP 2: CALLING API HANDLER ---");
        String apiResponse = rebookingApi.rebookingDapi(intentPayload);
        
        // Step 3: Format response for LLM consumption
        System.out.println("\n--- STEP 3: FORMATTING RESPONSE FOR LLM ---");
        String formattedResponse = formatRebookingResponseForLlm(apiResponse, pnr, lastName, newDepartureDate, newFlightNumber);
        
        System.out.println("Final Response to LLM:");
        System.out.println(formattedResponse);
        System.out.println("=== END REBOOKING TOOL ===");
        
        return formattedResponse;
    }
    
    private String formatRebookingResponseForLlm(String apiResponse, String pnr, String lastName, String newDepartureDate, String newFlightNumber) {
        return String.format("""
            <h3>Rebooking Completed Successfully</h3>
            
            <p><b>Original PNR:</b> %s</p>
            <p><b>New PNR:</b> %s-NEW</p>
            <p><b>Passenger:</b> John %s</p>
            
            <p><b>Original Flight:</b> AB1234 (Cancelled)</p>
            <p><b>New Flight:</b> %s</p>
            <p><b>New Departure Date:</b> %s</p>
            <p><b>New Route:</b> New York (JFK) → Los Angeles (LAX)</p>
            <p><b>New Departure Time:</b> %s at 2:30 PM</p>
            <p><b>New Arrival Time:</b> %s at 5:45 PM</p>
            <p><b>New Seat:</b> 15C</p>
            <p><b>Status:</b> Confirmed</p>
            
            <p><b>Rebooking Fee:</b> $75.00 USD</p>
            
            <p>Your flight has been successfully rebooked. You will receive a confirmation email with your new e-ticket.</p>
            """, pnr, pnr, lastName, newFlightNumber, newDepartureDate, newDepartureDate, newDepartureDate);
    }
} 