package org.acme.tools.master;

import org.acme.agents.FlightSearchAgent;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FlightSearchAgentTool {

    @Inject
    FlightSearchAgent flightSearchAgent;

    @Tool("Delegate flight search and rebooking queries to the FlightSearchAgent. Use this for flight searches, rebooking, and flight status inquiries.")
    public String handleFlightQuery(
            @P("The session ID for conversation continuity") String sessionId,
            @P("The customer's flight-related query or request") String query) {
        
        System.out.println("=== FLIGHT SEARCH AGENT TOOL CALLED ===");
        System.out.println("Session ID: " + sessionId);
        System.out.println("Query: " + query);
        
        // Delegate to the specialized FlightSearchAgent
        String response = flightSearchAgent.chat(sessionId, query);
        
        System.out.println("FlightSearchAgent Response: " + response);
        System.out.println("=== END FLIGHT SEARCH AGENT TOOL ===");
        
        return response;
    }
} 