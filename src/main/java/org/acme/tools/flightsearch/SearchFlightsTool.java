package org.acme.tools.flightsearch;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SearchFlightsTool {

    @Tool("Search available flights for new bookings between origin and destination on a specific date.")
    public String searchFlights(@P("The origin airport code or city") String origin,
                               @P("The destination airport code or city") String destination,
                               @P("The departure date in YYYY-MM-DD format") String date) {
        
        System.out.println("=== SEARCH FLIGHTS TOOL CALLED ===");
        System.out.println("Tool: searchFlights");
        System.out.println("LLM Extracted Parameters:");
        System.out.println("  - origin: " + origin);
        System.out.println("  - destination: " + destination);
        System.out.println("  - date: " + date);
        
        // Mock flight search results
        String searchResults = formatFlightSearchResults(origin, destination, date);
        
        System.out.println("Final Response to LLM:");
        System.out.println(searchResults);
        System.out.println("=== END SEARCH FLIGHTS TOOL ===");
        
        return searchResults;
    }
    
    private String formatFlightSearchResults(String origin, String destination, String date) {
        return String.format("""
            <h3>Available Flights from %s to %s on %s</h3>
            
            <ul>
                <li><b>Flight AB1234</b> - Departure: 10:00 AM, Arrival: 1:00 PM<br>
                    Price: $299 USD | Economy Available | 3h 0m duration</li>
                <li><b>Flight CD5678</b> - Departure: 2:30 PM, Arrival: 5:45 PM<br>
                    Price: $349 USD | Economy Available | 3h 15m duration</li>
                <li><b>Flight EF9012</b> - Departure: 7:00 PM, Arrival: 10:30 PM<br>
                    Price: $279 USD | Economy Available | 3h 30m duration</li>
            </ul>
            
            <p>All flights include free carry-on baggage. Additional fees may apply for checked luggage and seat selection.</p>
            <p>To book any of these flights, please provide passenger details and payment information.</p>
            """, origin, destination, date);
    }
} 