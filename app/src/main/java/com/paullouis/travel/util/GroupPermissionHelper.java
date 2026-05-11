package com.paullouis.travel.util;

import com.paullouis.travel.model.Group;

public class GroupPermissionHelper {

    public static boolean canManageMembers(Group group) {
        if (group == null) return false;
        Group.UserRole role = group.getRole();
        return role == Group.UserRole.OWNER || role == Group.UserRole.ADMIN;
    }

    public static boolean canModerateContent(Group group) {
        if (group == null) return false;
        Group.UserRole role = group.getRole();
        return role == Group.UserRole.OWNER || role == Group.UserRole.ADMIN || role == Group.UserRole.MODERATOR;
    }

    public static boolean canEditSettings(Group group) {
        if (group == null) return false;
        Group.UserRole role = group.getRole();
        return role == Group.UserRole.OWNER || role == Group.UserRole.ADMIN;
    }

    public static boolean isOwner(Group group) {
        if (group == null) return false;
        return group.getRole() == Group.UserRole.OWNER;
    }
}
