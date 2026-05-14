package com.paullouis.travel.util;

import com.paullouis.travel.model.Group;

public class GroupPermissionHelper {

    public static boolean canManageMembers(Group group) {
        if (group == null) return false;
        return isOwner(group);
    }

    public static boolean canModerateContent(Group group) {
        if (group == null) return false;
        return isOwner(group);
    }

    public static boolean canEditSettings(Group group) {
        if (group == null) return false;
        return isOwner(group);
    }

    public static boolean isOwner(Group group) {
        if (group == null) return false;
        String currentUserId = com.paullouis.travel.data.FirebaseRepository.getInstance().getCurrentUserId();
        return currentUserId != null && currentUserId.equals(group.getOwnerId());
    }
}
