package org.acme.tools.master;

import org.acme.agents.CustomerServiceAgent;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CustomerServiceAgentTool {

    @Inject
    CustomerServiceAgent customerServiceAgent;

    @Tool("Delegate general customer service queries to the CustomerServiceAgent. Use this for policies, contact information, and FAQ inquiries.")
    public String handleCustomerServiceQuery(
            @P("The session ID for conversation continuity") String sessionId,
            @P("The customer's general service query or request") String query) {
        
        System.out.println("=== CUSTOMER SERVICE AGENT TOOL CALLED ===");
        System.out.println("Session ID: " + sessionId);
        System.out.println("Query: " + query);
        
        // Delegate to the specialized CustomerServiceAgent
        String response = customerServiceAgent.chat(sessionId, query);
        
        System.out.println("CustomerServiceAgent Response: " + response);
        System.out.println("=== END CUSTOMER SERVICE AGENT TOOL ===");
        
        return response;
    }
} 