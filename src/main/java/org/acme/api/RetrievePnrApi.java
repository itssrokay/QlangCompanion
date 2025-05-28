package org.acme.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class RetrievePnrApi {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String retrievePnrDapi(String payload) {
        System.out.println("=== RETRIEVE_PNR API HANDLER CALLED ===");
        System.out.println("API Handler: retrievePnrDapi");
        System.out.println("Received Payload:");
        System.out.println(payload);
        
        try {
            // Parse the payload to extract parameters
            JsonNode payloadNode = objectMapper.readTree(payload);
            String pnr = payloadNode.get("pnr").asText();
            String lastName = payloadNode.get("lastName").asText();
            
            System.out.println("Extracted Parameters:");
            System.out.println("  - pnr: " + pnr);
            System.out.println("  - lastName: " + lastName);
            
            // Check if real DAPI configuration is available at runtime
            String dapiEndpoint = getDapiEndpoint();
            String dapiAuthToken = getDapiAuthToken();
            
            if (dapiEndpoint != null && !dapiEndpoint.isEmpty() && 
                dapiAuthToken != null && !dapiAuthToken.isEmpty()) {
                
                System.out.println("Using REAL DAPI API");
                return callRealDapiApi(pnr, lastName, dapiEndpoint, dapiAuthToken);
                
            } else {
                System.out.println("DAPI configuration not available, using MOCK data");
                return getMockResponse(pnr, lastName);
            }
            
        } catch (Exception e) {
            System.out.println("Error processing request: " + e.getMessage());
            e.printStackTrace();
            
            // Fall back to mock data on any error
            System.out.println("Falling back to mock data due to error");
            return getMockResponse("ERROR", "ERROR");
        }
    }
    
    private String getDapiEndpoint() {
        // Try environment variable first, then system property
        String endpoint = System.getenv("DAPI_ENDPOINT");
        if (endpoint == null || endpoint.isEmpty()) {
            endpoint = System.getProperty("dapi.endpoint", "");
        }
        return endpoint;
    }
    
    private String getDapiAuthToken() {
        // Try environment variable first, then system property
        String token = System.getenv("DAPI_1AAUTH_TOKEN_AC");
        if (token == null || token.isEmpty()) {
            token = System.getProperty("dapi.auth.token", "");
        }
        return token;
    }
    
    private String getDapiPurchaseOrder() {
        // Try environment variable first, then system property, then default
        String purchaseOrder = System.getenv("DAPI_PURCHASE_ORDER");
        if (purchaseOrder == null || purchaseOrder.isEmpty()) {
            purchaseOrder = System.getProperty("dapi.purchase.order", "/orders/");
        }
        return purchaseOrder;
    }
    
    private String callRealDapiApi(String pnr, String lastName, String dapiEndpoint, String dapiAuthToken) {
        // Build the API URL: {DAPI_ENDPOINT}{DAPI_PURCHASE_ORDER}{pnr}
        String dapiPurchaseOrder = getDapiPurchaseOrder();
        String apiUrl = dapiEndpoint + dapiPurchaseOrder + pnr;
        System.out.println("Making API call to: " + apiUrl);
        
        // Create HTTP client
        Client client = ClientBuilder.newClient();
        
        try {
            // Make the API call with authentication
            Response response = client.target(apiUrl)
                .queryParam("lastName", lastName)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "1AAuth " + dapiAuthToken)
                .get();
            
            System.out.println("Response Status Code: " + response.getStatus());
            
            if (response.getStatus() == 200) {
                String responseBody = response.readEntity(String.class);
                System.out.println("API Response Content:");
                System.out.println(responseBody);
                System.out.println("=== END RETRIEVE_PNR API HANDLER ===");
                
                return responseBody;
            } else {
                String errorBody = response.readEntity(String.class);
                System.out.println("API Error Response:");
                System.out.println(errorBody);
                
                // Return error response in expected format
                String errorResponse = String.format("""
                    {
                        "status": "error",
                        "code": %d,
                        "message": "API call failed",
                        "details": %s
                    }
                    """, response.getStatus(), errorBody != null ? "\"" + errorBody.replace("\"", "\\\"") + "\"" : "null");
                
                return errorResponse;
            }
            
        } finally {
            client.close();
        }
    }
    
    private String getMockResponse(String pnr, String lastName) {
        // Mock API response simulating real PNR data
        String mockResponse = String.format("""
            {
                "status": "success",
                "source": "mock",
                "data": {
                    "pnr": "%s",
                    "passengerName": "John %s",
                    "flightNumber": "AB1234",
                    "departure": {
                        "airport": "JFK",
                        "city": "New York",
                        "dateTime": "2024-03-25T10:00:00"
                    },
                    "arrival": {
                        "airport": "LAX", 
                        "city": "Los Angeles",
                        "dateTime": "2024-03-25T13:00:00"
                    },
                    "seatNumber": "12A",
                    "class": "Economy",
                    "status": "Confirmed"
                }
            }
            """, pnr, lastName);
        
        System.out.println("Mock API Response:");
        System.out.println(mockResponse);
        System.out.println("=== END RETRIEVE_PNR API HANDLER ===");
        
        return mockResponse;
    }
} 