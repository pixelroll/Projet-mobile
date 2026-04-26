package com.paullouis.travel.model;

public class ReportedPhoto {
    public enum Status {
        PENDING, RESOLVED, REJECTED
    }

    private String id;
    private Photo photo;
    private String reason;
    private String reporterName;
    private String date;
    private Status status;

    public ReportedPhoto(String id, Photo photo, String reason, String reporterName, String date) {
        this.id = id;
        this.photo = photo;
        this.reason = reason;
        this.reporterName = reporterName;
        this.date = date;
        this.status = Status.PENDING;
    }

    // Getters and Setters
    public String getId() { return id; }
    public Photo getPhoto() { return photo; }
    public String getReason() { return reason; }
    public String getReporterName() { return reporterName; }
    public String getDate() { return date; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
