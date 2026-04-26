package com.paullouis.travel.model;

public class SearchNavigationOption {
    private String id;
    private String title;
    private String subtitle;
    private int iconResId;
    private boolean isSelected;

    public SearchNavigationOption(String id, String title, String subtitle, int iconResId) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.iconResId = iconResId;
        this.isSelected = false;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public int getIconResId() { return iconResId; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
