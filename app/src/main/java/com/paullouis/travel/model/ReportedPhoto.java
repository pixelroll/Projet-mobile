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

    public ReportedPhoto() {
        this.status = Status.PENDING;
    }

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
    public void setId(String id) { this.id = id; }

    public Photo getPhoto() { return photo; }
    public void setPhoto(Photo photo) { this.photo = photo; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
