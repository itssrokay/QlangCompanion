package org.acme.tools.master;

import org.acme.agents.BookingAgent;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BookingAgentTool {

    @Inject
    BookingAgent bookingAgent;

    @Tool("Delegate booking-related queries to the BookingAgent. Use this for PNR retrieval, refund requests, available services, and booking modifications.")
    public String handleBookingQuery(
            @P("The session ID for conversation continuity") String sessionId,
            @P("The customer's booking-related query or request") String query) {
        
        System.out.println("=== BOOKING AGENT TOOL CALLED ===");
        System.out.println("Session ID: " + sessionId);
        System.out.println("Query: " + query);
        
        // Ensure we have a valid sessionId
        String actualSessionId = (sessionId != null && !sessionId.isBlank()) 
            ? sessionId 
            : "booking-session-" + System.currentTimeMillis();
        
        System.out.println("Using Session ID: " + actualSessionId);
        
        try {
            // Delegate to the specialized BookingAgent
            String response = bookingAgent.chat(actualSessionId, query);
            
            System.out.println("BookingAgent Response: " + response);
            System.out.println("=== END BOOKING AGENT TOOL ===");
            
            return response;
        } catch (Exception e) {
            System.err.println("Error in BookingAgent: " + e.getMessage());
            e.printStackTrace();
            return "I'm sorry, there was an error processing your booking request. Please provide your PNR and last name to retrieve your booking information.";
        }
    }
} 