package com.paullouis.travel.model;

public class TravelDestination {
    private int order;
    private String name;
    private String description;
    private String reason;
    private double latitude;
    private double longitude;
    private int estimatedPriceEuros;
    private int estimatedDurationMinutes;
    private String type;
    private int travelDurationMinutes;
    private String transportationMode;
    private boolean enabled = true;

    public TravelDestination() {}

    public TravelDestination(int order, String name, String description, String reason,
            double latitude, double longitude, int estimatedPriceEuros,
            int estimatedDurationMinutes, String type) {
        this.order = order;
        this.name = name;
        this.description = description;
        this.reason = reason;
        this.latitude = latitude;
        this.longitude = longitude;
        this.estimatedPriceEuros = estimatedPriceEuros;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.type = type;
    }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public int getEstimatedPriceEuros() { return estimatedPriceEuros; }
    public void setEstimatedPriceEuros(int estimatedPriceEuros) { this.estimatedPriceEuros = estimatedPriceEuros; }

    public int getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(int estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getTravelDurationMinutes() { return travelDurationMinutes; }
    public void setTravelDurationMinutes(int travelDurationMinutes) { this.travelDurationMinutes = travelDurationMinutes; }

    public String getTransportationMode() { return transportationMode; }
    public void setTransportationMode(String transportationMode) { this.transportationMode = transportationMode; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
