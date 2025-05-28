package org.acme.tools.flightsearch;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FlightStatusTool {

    @Tool("Get flight status and schedule information for a specific flight.")
    public String flightStatus(@P("The flight number") String flightNumber,
                              @P("The flight date in YYYY-MM-DD format") String date) {
        
        System.out.println("=== FLIGHT STATUS TOOL CALLED ===");
        System.out.println("Tool: flightStatus");
        System.out.println("LLM Extracted Parameters:");
        System.out.println("  - flightNumber: " + flightNumber);
        System.out.println("  - date: " + date);
        
        // Mock flight status information
        String statusInfo = formatFlightStatus(flightNumber, date);
        
        System.out.println("Final Response to LLM:");
        System.out.println(statusInfo);
        System.out.println("=== END FLIGHT STATUS TOOL ===");
        
        return statusInfo;
    }
    
    private String formatFlightStatus(String flightNumber, String date) {
        return String.format("""
            <h3>Flight Status: %s on %s</h3>
            
            <p><b>Status:</b> On Time</p>
            <p><b>Aircraft:</b> Boeing 737-800</p>
            <p><b>Route:</b> New York (JFK) → Los Angeles (LAX)</p>
            
            <p><b>Scheduled Departure:</b> 10:00 AM EST</p>
            <p><b>Actual Departure:</b> 10:05 AM EST</p>
            <p><b>Gate:</b> A12</p>
            <p><b>Terminal:</b> Terminal 4</p>
            
            <p><b>Scheduled Arrival:</b> 1:00 PM PST</p>
            <p><b>Estimated Arrival:</b> 1:10 PM PST</p>
            <p><b>Gate:</b> B8</p>
            <p><b>Terminal:</b> Terminal 6</p>
            
            <p><b>Flight Duration:</b> 6h 5m</p>
            <p><b>Distance:</b> 2,475 miles</p>
            
            <p>Please arrive at the gate at least 30 minutes before boarding time.</p>
            """, flightNumber, date);
    }
} 