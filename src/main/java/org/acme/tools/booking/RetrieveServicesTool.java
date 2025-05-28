package org.acme.tools.booking;

import org.acme.services.ServiceService;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RetrieveServicesTool {

    @Inject
    ServiceService serviceService;

    @Tool("Get available services for a booking using PNR. This tool will provide all additional services that can be purchased or added to the booking.")
    public String retrieveServices(@P("The PNR (record locator) for which to retrieve available services") String pnr) {
        System.out.println("=== SERVICE TOOL CALLED ===");
        System.out.println("PNR parameter received: " + pnr);
        
        // The LLM handles parameter extraction, we just call the service
        String result = serviceService.getServicesForPnr(pnr);
        
        // Format result as HTML
        String htmlResult = formatServicesAsHtml(result, pnr);
        
        System.out.println("=== SERVICE TOOL RESPONSE ===");
        System.out.println("Response length: " + htmlResult.length() + " characters");
        System.out.println("=== END SERVICE TOOL ===");
        
        return htmlResult;
    }
    
    private String formatServicesAsHtml(String servicesText, String pnr) {
        if (servicesText.contains("No additional services")) {
            return String.format("<p>No additional services are available for PNR: <b>%s</b></p>", pnr);
        }
        
        return String.format("""
            <h3>Available Services for PNR %s</h3>
            <ul>
                <li><b>Extra Baggage</b> - $50.00 USD<br>Additional 23kg baggage allowance</li>
                <li><b>Seat Selection</b> - $35.00 USD<br>Premium seat selection with extra legroom</li>
                <li><b>In-flight Meal</b> - $25.00 USD<br>Special dietary meal selection</li>
                <li><b>Priority Boarding</b> - $15.00 USD<br>Board the aircraft before general boarding</li>
            </ul>
            <p>Contact customer service to add any of these services to your booking.</p>
            """, pnr);
    }
}