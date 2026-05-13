package com.paullouis.travel.model;

public class NotificationSettingItem {
    public enum Type { PERSON, GROUP, PLACE, TAG, TYPE }

    private String subscriptionId; // Firestore document ID
    private String id;
    private Type type;
    private String name;
    private boolean enabled;

    public NotificationSettingItem() {}

    public NotificationSettingItem(String id, Type type, String name, boolean enabled) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.enabled = enabled;
    }

    public String getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }
    public String getId() { return id; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
