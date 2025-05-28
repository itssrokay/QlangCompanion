package org.acme.tools.customerservice;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ContactInfoTool {

    @Tool("Get contact information for different airline departments.")
    public String contactInfo(@P("The department to get contact information for") String department) {
        
        System.out.println("=== CONTACT INFO TOOL CALLED ===");
        System.out.println("Department: " + department);
        
        String contactDetails = getContactInformation(department.toLowerCase());
        
        System.out.println("Contact Details: " + contactDetails);
        System.out.println("=== END CONTACT INFO TOOL ===");
        
        return contactDetails;
    }
    
    private String getContactInformation(String department) {
        return switch (department) {
            case "customer service", "general", "support" -> """
                <h3>Customer Service</h3>
                <p><b>Phone:</b> 1-800-AIRLINE (1-800-247-5463)</p>
                <p><b>Hours:</b> 24/7</p>
                <p><b>Email:</b> support@airline.com</p>
                <p><b>Live Chat:</b> Available on our website</p>
                """;
            case "baggage", "lost baggage" -> """
                <h3>Baggage Services</h3>
                <p><b>Phone:</b> 1-800-BAG-HELP (1-800-224-4357)</p>
                <p><b>Hours:</b> 24/7</p>
                <p><b>Email:</b> baggage@airline.com</p>
                <p><b>Online:</b> Track your baggage at airline.com/baggage</p>
                """;
            case "reservations", "booking" -> """
                <h3>Reservations</h3>
                <p><b>Phone:</b> 1-800-FLY-HERE (1-800-359-4373)</p>
                <p><b>Hours:</b> Monday-Sunday 6:00 AM - 12:00 AM EST</p>
                <p><b>Online:</b> Book at airline.com</p>
                <p><b>Mobile App:</b> Download our mobile app for easy booking</p>
                """;
            default -> String.format("""
                <h3>Contact Information</h3>
                <p>For <b>%s</b> inquiries, please contact our main customer service line:</p>
                <p><b>Phone:</b> 1-800-AIRLINE (1-800-247-5463)</p>
                <p><b>Hours:</b> 24/7</p>
                <p>Our representatives will direct you to the appropriate department.</p>
                """, department);
        };
    }
} 