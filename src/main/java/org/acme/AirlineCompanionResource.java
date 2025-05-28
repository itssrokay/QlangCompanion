package org.acme;

import java.util.UUID;

import org.acme.agents.MasterAgent;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Main endpoint for the Multi-Agent Airline Companion System
 */
@Path("/companion")
public class AirlineCompanionResource {

    @Inject
    MasterAgent masterAgent;

    /**
     * Main chat endpoint - Routes to appropriate specialized agents
     */
    @GET
    @Path("/chat")
    @Produces(MediaType.APPLICATION_JSON)
    public Response chat(@QueryParam("q") String query, @QueryParam("sessionId") String sessionId) {
        System.out.println("=== MULTI-AGENT AIRLINE COMPANION ===");
        System.out.println("Query: " + query);
        System.out.println("Session ID: " + sessionId);
        
        if (query == null || query.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Query parameter 'q' is required\"}")
                    .build();
        }

        // Generate session ID if not provided - ALWAYS ensure we have a valid session ID
        String currentSessionId = (sessionId != null && !sessionId.isBlank()) 
            ? sessionId 
            : "session-" + UUID.randomUUID().toString();
        
        System.out.println("Using Session ID: " + currentSessionId);
        
        try {
            // Use MasterAgent to route queries to appropriate specialized agents
            String response = masterAgent.chat(currentSessionId, query);
            
            // Create JSON response
            String jsonResponse = String.format("""
                {
                    "chatId": "%s",
                    "response": "%s",
                    "responseType": "multi-agent-chat",
                    "architecture": "Master Agent → Specialized Agent → Tool → Intent → DAPI"
                }
                """, currentSessionId, escapeJson(response));
            
            System.out.println("Master Agent Response: " + response);
            System.out.println("=== END MULTI-AGENT COMPANION ===");
            
            return Response.ok(jsonResponse).build();
            
        } catch (Exception e) {
            System.err.println("Error processing request: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Internal server error\"}")
                    .build();
        }
    }

    /**
     * Health check endpoint
     */
    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public Response health() {
        return Response.ok("""
                Multi-Agent Airline Companion System is running!
                
                Architecture:
                Customer Query → Master Agent → Specialized Agent → Tool → Intent → DAPI → External API
                
                Available Agents:
                1. MasterAgent - Central orchestrator and query router
                2. BookingAgent - PNR retrieval, refunds, services  
                3. FlightSearchAgent - Flight search, rebooking, status
                4. CustomerServiceAgent - Policies, contact info, FAQs
                
                Available endpoints:
                /companion/chat - Main multi-agent chat interface
                /companion/health - Health check
                /companion/test - Test different agent scenarios
                
                Example usage:
                /companion/chat?q=Check my booking BS8ND5 for WICK&sessionId=test123
                /companion/chat?q=Search flights from NYC to LAX on 2024-04-15&sessionId=test456
                /companion/chat?q=What is your baggage policy?&sessionId=test789
                """).build();
    }

    /**
     * Test different agent scenarios
     */
    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testScenario(@QueryParam("scenario") String scenario, @QueryParam("sessionId") String sessionId) {
        if (scenario == null || scenario.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Scenario parameter required. Options: booking, flight-search, customer-service\"}")
                    .build();
        }

        String testQuery = getTestQuery(scenario);
        if (testQuery == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid scenario. Options: booking, flight-search, customer-service\"}")
                    .build();
        }

        return chat(testQuery, sessionId != null ? sessionId : scenario + "-test");
    }

    private String getTestQuery(String scenario) {
        return switch (scenario.toLowerCase()) {
            case "booking" -> "Check my booking BS8ND5 for passenger WICK";
            case "flight-search" -> "Search flights from New York to Los Angeles on April 15, 2024";
            case "customer-service" -> "What is your baggage policy?";
            default -> null;
        };
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
} 