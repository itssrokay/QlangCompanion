package org.acme.tools.customerservice;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FaqTool {

    @Tool("Answer frequently asked questions about airline services.")
    public String faq(@P("The question or topic to get FAQ information about") String question) {
        
        System.out.println("=== FAQ TOOL CALLED ===");
        System.out.println("Question: " + question);
        
        String faqResponse = getFaqAnswer(question.toLowerCase());
        
        System.out.println("FAQ Response: " + faqResponse);
        System.out.println("=== END FAQ TOOL ===");
        
        return faqResponse;
    }
    
    private String getFaqAnswer(String question) {
        if (question.contains("check") && question.contains("in")) {
            return """
                <h3>Online Check-in</h3>
                <p><b>When:</b> 24 hours to 1 hour before departure</p>
                <p><b>How:</b> Visit airline.com or use our mobile app</p>
                <p><b>Required:</b> Confirmation number and last name</p>
                <p><b>Benefits:</b> Skip airport check-in lines, choose seats, add services</p>
                """;
        } else if (question.contains("id") || question.contains("document")) {
            return """
                <h3>Travel Documents</h3>
                <p><b>Domestic Travel:</b> Government-issued photo ID (driver's license, passport, etc.)</p>
                <p><b>International Travel:</b> Valid passport (and visa if required)</p>
                <p><b>Children:</b> Birth certificate for under 18 (domestic), passport for international</p>
                <p>Check destination requirements at airline.com/travel-info</p>
                """;
        } else if (question.contains("seat") || question.contains("upgrade")) {
            return """
                <h3>Seat Selection & Upgrades</h3>
                <p><b>Free Seats:</b> Basic seats available during check-in</p>
                <p><b>Preferred Seats:</b> Extra legroom seats for $25-50</p>
                <p><b>Upgrades:</b> Available based on fare type and availability</p>
                <p>Select seats during booking, check-in, or manage your reservation online</p>
                """;
        } else {
            return String.format("""
                <h3>Frequently Asked Questions</h3>
                <p>I don't have a specific answer for "<b>%s</b>" in my FAQ database.</p>
                <p><b>Popular topics include:</b></p>
                <ul>
                    <li>Check-in procedures</li>
                    <li>Travel documents and ID requirements</li>
                    <li>Seat selection and upgrades</li>
                    <li>Baggage policies</li>
                    <li>Flight changes and cancellations</li>
                </ul>
                <p>For specific questions, please contact customer service at 1-800-AIRLINE.</p>
                """, question);
        }
    }
} 