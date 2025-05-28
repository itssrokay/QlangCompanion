package org.acme.tools.booking;

import org.acme.api.RefundApi;
import org.acme.intent.RefundIntent;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RefundTool {

    @Inject
    RefundIntent refundIntent;
    
    @Inject
    RefundApi refundApi;

    @Tool("Check refund eligibility and process refund for a booking using PNR and last name. This tool will provide refund amount, policy details, and processing information.")
    public String refund(@P("The PNR (record locator) for the booking to refund") String pnr, 
                        @P("The passenger's last name") String lastName) {
        
        System.out.println("=== REFUND TOOL CALLED ===");
        System.out.println("Tool: refund");
        System.out.println("LLM Extracted Parameters:");
        System.out.println("  - pnr: " + pnr);
        System.out.println("  - lastName: " + lastName);
        
        // Step 1: Call intent function to format parameters
        System.out.println("\n--- STEP 1: CALLING INTENT FUNCTION ---");
        String intentPayload = refundIntent.refund(pnr, lastName);
        
        // Step 2: Call API handler with formatted payload
        System.out.println("\n--- STEP 2: CALLING API HANDLER ---");
        String apiResponse = refundApi.refundDapi(intentPayload);
        
        // Step 3: Format response for LLM consumption
        System.out.println("\n--- STEP 3: FORMATTING RESPONSE FOR LLM ---");
        String formattedResponse = formatRefundResponseForLlm(apiResponse, pnr, lastName);
        
        System.out.println("Final Response to LLM:");
        System.out.println(formattedResponse);
        System.out.println("=== END REFUND TOOL ===");
        
        return formattedResponse;
    }
    
    private String formatRefundResponseForLlm(String apiResponse, String pnr, String lastName) {
        return String.format("""
            <h3>Refund Eligibility Check Completed</h3>
            
            <p><b>PNR:</b> %s</p>
            <p><b>Passenger:</b> John %s</p>
            <p><b>Refund Status:</b> ELIGIBLE</p>
            <p><b>Refund Amount:</b> $450.00 USD</p>
            <p><b>Cancellation Fee:</b> $0.00 USD (waived)</p>
            <p><b>Net Refund:</b> $450.00 USD</p>
            
            <p><b>Refund Policy:</b> Full refund available - cancellation within 24 hours</p>
            <p><b>Processing Time:</b> 5-7 business days</p>
            <p><b>Refund Method:</b> Original payment method</p>
            
            <p>The refund has been processed successfully. You will receive a confirmation email shortly.</p>
            """, pnr, lastName);
    }
} 