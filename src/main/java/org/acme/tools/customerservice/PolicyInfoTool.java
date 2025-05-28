package org.acme.tools.customerservice;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PolicyInfoTool {

    @Tool("Get information about airline policies such as baggage, cancellation, refund, check-in procedures, etc.")
    public String policyInfo(@P("The policy topic to get information about") String topic) {
        
        System.out.println("=== POLICY INFO TOOL CALLED ===");
        System.out.println("Tool: policyInfo");
        System.out.println("Topic: " + topic);
        
        String policyResponse = getPolicyInformation(topic.toLowerCase());
        
        System.out.println("Policy Response: " + policyResponse);
        System.out.println("=== END POLICY INFO TOOL ===");
        
        return policyResponse;
    }
    
    private String getPolicyInformation(String topic) {
        return switch (topic) {
            case "baggage", "luggage" -> """
                <h3>Baggage Policy</h3>
                <p><b>Carry-on Baggage:</b></p>
                <ul>
                    <li>Maximum dimensions: 22" x 14" x 9"</li>
                    <li>Maximum weight: 22 lbs (10 kg)</li>
                    <li>One carry-on and one personal item allowed</li>
                </ul>
                <p><b>Checked Baggage:</b></p>
                <ul>
                    <li>First bag: $30 USD</li>
                    <li>Second bag: $40 USD</li>
                    <li>Maximum weight: 50 lbs (23 kg)</li>
                    <li>Maximum dimensions: 62" total (length + width + height)</li>
                </ul>
                """;
            case "cancellation", "cancel" -> """
                <h3>Cancellation Policy</h3>
                <p><b>24-Hour Rule:</b> Cancel within 24 hours of booking for full refund</p>
                <p><b>Standard Cancellation:</b></p>
                <ul>
                    <li>Basic Economy: Non-refundable, change fee applies</li>
                    <li>Main Cabin: $200 change fee + fare difference</li>
                    <li>Premium/Business: No change fee, pay fare difference only</li>
                </ul>
                <p>Cancellations can be processed online or by calling customer service.</p>
                """;
            case "refund" -> """
                <h3>Refund Policy</h3>
                <p><b>Refundable Tickets:</b> Full refund to original payment method</p>
                <p><b>Non-refundable Tickets:</b> Credit for future travel (minus applicable fees)</p>
                <p><b>Processing Time:</b> 5-7 business days for credit cards, 2-3 weeks for other payment methods</p>
                <p>Special circumstances (illness, military duty) may qualify for exceptions.</p>
                """;
            default -> String.format("""
                <h3>General Airline Policies</h3>
                <p>For information about <b>%s</b>, please contact our customer service team.</p>
                <p><b>Common policies available:</b></p>
                <ul>
                    <li>Baggage and luggage rules</li>
                    <li>Cancellation and change policies</li>
                    <li>Refund procedures</li>
                    <li>Check-in requirements</li>
                    <li>Special assistance services</li>
                </ul>
                """, topic);
        };
    }
} 