package com.paullouis.travel.model;

public class NotificationSettingItem {
    public enum Type { PERSON, GROUP, PLACE, TAG }

    private String id;
    private Type type;
    private String name;
    private boolean enabled;

    public NotificationSettingItem(String id, Type type, String name, boolean enabled) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.enabled = enabled;
    }

    public String getId() { return id; }
    public Type getType() { return type; }
    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
