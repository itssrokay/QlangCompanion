package org.acme.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acme.models.ServiceResponse;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Service to handle airline service data retrieval
 */
@ApplicationScoped
public class ServiceService {
    
    // Mock database of services for different PNRs (in a real app, this would call an external API)
    private final Map<String, List<ServiceResponse>> serviceData = new HashMap<>();

    public ServiceService() {
        // Initialize with sample service data for different PNRs
        initializeMockData();
    }

    private void initializeMockData() {
        // Services for PNR: BS8ND5
        List<ServiceResponse> bs8nd5Services = new ArrayList<>();
        bs8nd5Services.add(new ServiceResponse("SVC001", "Extra Baggage", "Additional 23kg baggage allowance", "50.00", "USD", "AB1234", "BS8ND5"));
        bs8nd5Services.add(new ServiceResponse("SVC002", "Seat Selection", "Premium seat selection with extra legroom", "35.00", "USD", "AB1234", "BS8ND5"));
        bs8nd5Services.add(new ServiceResponse("SVC003", "In-flight Meal", "Special dietary meal selection", "25.00", "USD", "AB1234", "BS8ND5"));
        bs8nd5Services.add(new ServiceResponse("SVC004", "Priority Boarding", "Board the aircraft before general boarding", "15.00", "USD", "AB1234", "BS8ND5"));
        serviceData.put("BS8ND5", bs8nd5Services);

        // Services for PNR: 6X67GH
        List<ServiceResponse> sample1Services = new ArrayList<>();
        sample1Services.add(new ServiceResponse("SVC005", "Wheelchair Assistance", "Wheelchair assistance throughout the journey", "0.00", "USD", "LH456", "6X67GH"));
        sample1Services.add(new ServiceResponse("SVC006", "Pet Transport", "Transport your pet in the cabin", "100.00", "USD", "LH456", "6X67GH"));
        sample1Services.add(new ServiceResponse("SVC007", "Lounge Access", "Access to premium airport lounge", "45.00", "USD", "LH456", "6X67GH"));
        serviceData.put("6X67GH", sample1Services);

        // Services for PNR: HUG67D
        List<ServiceResponse> sample2Services = new ArrayList<>();
        sample2Services.add(new ServiceResponse("SVC008", "Fast Track Security", "Skip the regular security queue", "20.00", "USD", "BA789", "HUG67D"));
        sample2Services.add(new ServiceResponse("SVC009", "Travel Insurance", "Comprehensive travel insurance coverage", "75.00", "USD", "BA789", "HUG67D"));
        sample2Services.add(new ServiceResponse("SVC010", "Car Rental", "Discounted car rental at destination", "120.00", "USD", "BA789", "HUG67D"));
        serviceData.put("HUG67D", sample2Services);
    }

    /**
     * Get available services for a specific PNR
     * @param pnr The PNR (record locator)
     * @return Formatted service information with usage instructions
     */
    public String getServicesForPnr(String pnr) {
        System.out.println("=== SERVICE RETRIEVAL DEBUG ===");
        System.out.println("Input PNR: " + pnr);
        
        List<ServiceResponse> services = getServiceData(pnr);
        System.out.println("Found " + services.size() + " services for PNR: " + pnr);
        
        if (services.isEmpty()) {
            String noServicesMessage = "No additional services are available for PNR: " + pnr + 
                                     "\n\nThis could mean either the booking is not found or no additional services are offered for this route.";
            System.out.println("Returning: " + noServicesMessage);
            return noServicesMessage;
        }

        StringBuilder response = new StringBuilder();
        response.append("Available services for PNR ").append(pnr).append(":\n\n");
        
        for (ServiceResponse service : services) {
            System.out.println("Processing service: " + service.getServiceName() + " - " + service.getPrice() + " " + service.getCurrency());
            
            response.append("• ").append(service.getServiceName());
            if (!"0.00".equals(service.getPrice())) {
                response.append(" - ").append(service.getPrice()).append(" ").append(service.getCurrency());
            } else {
                response.append(" - Complimentary");
            }
            response.append("\n  ").append(service.getDescription());
            response.append("\n  Flight: ").append(service.getFlightNumber());
            response.append("\n  Service ID: ").append(service.getServiceId()).append("\n\n");
        }
        
        response.append("Usage instructions: These services can be added to your booking. ");
        response.append("Contact customer service with the Service ID to add any of these services to PNR ").append(pnr).append(".");
        
        String finalResponse = response.toString();
        System.out.println("=== FINAL SERVICE RESPONSE ===");
        System.out.println(finalResponse);
        System.out.println("=== END DEBUG ===");
        
        return finalResponse;
    }

    // Internal method to get service data
    private List<ServiceResponse> getServiceData(String pnr) {
        String pnrUpper = pnr.toUpperCase();
        System.out.println("Looking up services for PNR (uppercase): " + pnrUpper);
        
        if (serviceData.containsKey(pnrUpper)) {
            List<ServiceResponse> found = serviceData.get(pnrUpper);
            System.out.println("Found " + found.size() + " services in mock data");
            return found;
        } else {
            System.out.println("PNR not found in mock data, returning empty list");
            // For unknown PNRs, return empty list (in real app, this would call external API)
            return new ArrayList<>();
        }
    }
} 