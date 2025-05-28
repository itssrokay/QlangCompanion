package org.acme.models;

/**
 * Service information response model
 */
public class ServiceResponse {
    private String serviceId;
    private String serviceName;
    private String description;
    private String price;
    private String currency;
    private String flightNumber;
    private String pnr;

    public ServiceResponse(String serviceId, String serviceName, String description, 
                          String price, String currency, String flightNumber, String pnr) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.flightNumber = flightNumber;
        this.pnr = pnr;
    }

    // Getters and setters
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }
} 