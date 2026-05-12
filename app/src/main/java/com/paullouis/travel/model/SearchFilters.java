package com.paullouis.travel.model;

import java.util.List;

public class SearchFilters {
    private String query;
    private String placeType;
    private String momentOfDay;
    private String period;
    private String location;
    private Double latitude;
    private Double longitude;
    private Integer radiusKm;
    private String author;
    private String groupId;
    private List<String> tags;

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public String getPlaceType() { return placeType; }
    public void setPlaceType(String placeType) { this.placeType = placeType; }
    public String getMomentOfDay() { return momentOfDay; }
    public void setMomentOfDay(String momentOfDay) { this.momentOfDay = momentOfDay; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public Integer getRadiusKm() { return radiusKm; }
    public void setRadiusKm(Integer radiusKm) { this.radiusKm = radiusKm; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
