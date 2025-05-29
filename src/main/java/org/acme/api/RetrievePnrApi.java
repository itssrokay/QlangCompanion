package org.acme.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;

import okhttp3.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class RetrievePnrApi {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    Config config;

    // Create insecure OkHttpClient
    private static final OkHttpClient INSECURE_CLIENT = createInsecureClient();

    private static OkHttpClient createInsecureClient() {
        try {
            // Trust all certificates
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true) // Disable hostname check
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

        } catch (Exception e) {
            System.out.println("Failed to create insecure client: " + e.getMessage());
            throw new RuntimeException("Failed to create insecure client", e);
        }
    }

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
            
            // Get DAPI configuration
            String dapiEndpoint = getDapiEndpoint();
            String dapiAuthToken = getDapiAuthToken();
            
            System.out.println("Configuration Check:");
            System.out.println("  - DAPI Endpoint: " + (dapiEndpoint != null && !dapiEndpoint.isEmpty() ? dapiEndpoint : "NOT SET"));
            System.out.println("  - DAPI Token: " + (dapiAuthToken != null && !dapiAuthToken.isEmpty() ? "***SET***" : "NOT SET"));
            
            // Validate required configuration
            if (dapiEndpoint == null || dapiEndpoint.isEmpty()) {
                String errorResponse = """
                    {
                        "status": "error",
                        "message": "DAPI endpoint not configured. Please set dapi.endpoint in application.properties or DAPI_ENDPOINT environment variable."
                    }
                    """;
                System.out.println("ERROR: DAPI endpoint not configured");
                System.out.println("=== END RETRIEVE_PNR API HANDLER (CONFIG ERROR) ===");
                return errorResponse;
            }
            
            if (dapiAuthToken == null || dapiAuthToken.isEmpty()) {
                String errorResponse = """
                    {
                        "status": "error",
                        "message": "DAPI auth token not configured. Please set dapi.auth.token in application.properties or DAPI_1AAUTH_TOKEN_AC environment variable."
                    }
                    """;
                System.out.println("ERROR: DAPI auth token not configured");
                System.out.println("=== END RETRIEVE_PNR API HANDLER (CONFIG ERROR) ===");
                return errorResponse;
            }
            
            // Call real DAPI API using OkHttp3
            System.out.println("Calling REAL DAPI API with OkHttp3");
            return callRealDapiApi(pnr, lastName, dapiEndpoint, dapiAuthToken);
            
        } catch (Exception e) {
            System.out.println("Error processing request: " + e.getMessage());
            e.printStackTrace();
            
            String errorResponse = String.format("""
                {
                    "status": "error",
                    "message": "Failed to process PNR request",
                    "error": "%s"
                }
                """, e.getMessage().replace("\"", "\\\""));
            
            System.out.println("=== END RETRIEVE_PNR API HANDLER (ERROR) ===");
            return errorResponse;
        }
    }
    
    private String getDapiEndpoint() {
        // Try environment variable first
        String endpoint = System.getenv("DAPI_ENDPOINT");
        if (endpoint != null && !endpoint.isEmpty()) {
            return endpoint;
        }
        
        // Try application.properties via Quarkus Config
        try {
            return config.getOptionalValue("dapi.endpoint", String.class).orElse("");
        } catch (Exception e) {
            System.out.println("Error reading dapi.endpoint config: " + e.getMessage());
            return "";
        }
    }
    
    private String getDapiAuthToken() {
        // Try environment variable first
        String token = System.getenv("DAPI_1AAUTH_TOKEN_AC");
        if (token != null && !token.isEmpty()) {
            return token;
        }
        
        // Try application.properties via Quarkus Config
        try {
            return config.getOptionalValue("dapi.auth.token", String.class).orElse("");
        } catch (Exception e) {
            System.out.println("Error reading dapi.auth.token config: " + e.getMessage());
            return "";
        }
    }
    
    private String getDapiPurchaseOrder() {
        // Try environment variable first
        String purchaseOrder = System.getenv("DAPI_PURCHASE_ORDER");
        if (purchaseOrder != null && !purchaseOrder.isEmpty()) {
            return purchaseOrder;
        }
        
        // Try application.properties via Quarkus Config, with default
        try {
            return config.getOptionalValue("dapi.purchase.order", String.class).orElse("/orders/");
        } catch (Exception e) {
            System.out.println("Error reading dapi.purchase.order config: " + e.getMessage());
            return "/orders/";
        }
    }
    
    private String callRealDapiApi(String pnr, String lastName, String dapiEndpoint, String dapiAuthToken) {
        // Build the API URL: {DAPI_ENDPOINT}{DAPI_PURCHASE_ORDER}{pnr}
        String dapiPurchaseOrder = getDapiPurchaseOrder();
        String baseUrl = dapiEndpoint.endsWith("/") ? dapiEndpoint : dapiEndpoint + "/";
        String purchaseOrderPath = dapiPurchaseOrder.startsWith("/") ? dapiPurchaseOrder.substring(1) : dapiPurchaseOrder;
        String apiUrl = baseUrl + purchaseOrderPath + pnr + "?lastName=" + lastName;
        
        System.out.println("Making OkHttp3 API call to: " + apiUrl);
        System.out.println("With Authorization: 1AAuth " + dapiAuthToken.substring(0, Math.min(dapiAuthToken.length(), 10)) + "...");
        
        try {
            // Build the request
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .header("Authorization", "1AAuth " + dapiAuthToken)
                    .header("Accept", "application/json")
                    .get()
                    .build();
            
            // Execute the request
            try (Response response = INSECURE_CLIENT.newCall(request).execute()) {
                System.out.println("Response Status Code: " + response.code());
                
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    System.out.println("API Response Content:");
                    System.out.println(responseBody);
                    System.out.println("=== END RETRIEVE_PNR API HANDLER (SUCCESS) ===");
                    
                    return responseBody;
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    System.out.println("API Error Response:");
                    System.out.println("Status: " + response.code());
                    System.out.println("Body: " + errorBody);
                    
                    // Return error response in expected format
                    String errorResponse = String.format("""
                        {
                            "status": "error",
                            "code": %d,
                            "message": "DAPI API call failed",
                            "details": %s,
                            "url": "%s"
                        }
                        """, response.code(), 
                             errorBody != null && !errorBody.isEmpty() ? "\"" + errorBody.replace("\"", "\\\"") + "\"" : "null",
                             apiUrl);
                    
                    System.out.println("=== END RETRIEVE_PNR API HANDLER (API ERROR) ===");
                    return errorResponse;
                }
            }
            
        } catch (Exception e) {
            System.out.println("Exception during API call: " + e.getMessage());
            e.printStackTrace();
            
            String errorResponse = String.format("""
                {
                    "status": "error",
                    "message": "Failed to connect to DAPI",
                    "error": "%s",
                    "url": "%s"
                }
                """, e.getMessage().replace("\"", "\\\""), apiUrl);
            
            System.out.println("=== END RETRIEVE_PNR API HANDLER (CONNECTION ERROR) ===");
            return errorResponse;
        }
    }
} 