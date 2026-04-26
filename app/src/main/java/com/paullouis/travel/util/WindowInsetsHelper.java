package com.paullouis.travel.util;

import android.app.Activity;
import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Centralized helper to apply window insets (status bar, navigation bar)
 * to a given anchor view, avoiding duplicate boilerplate across Activities.
 */
public final class WindowInsetsHelper {

    private WindowInsetsHelper() {}

    /**
     * Applies top status-bar padding to the given view.
     * Typically used on a Toolbar or AppBarLayout.
     */
    public static void applyStatusBarPadding(View anchorView) {
        ViewCompat.setOnApplyWindowInsetsListener(anchorView, (v, insets) -> {
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, statusBars.top, 0, 0);
            return insets;
        });
    }

    /**
     * Applies top status-bar padding + bottom navigation-bar padding
     * to the root content view. Useful for full-screen activities.
     */
    public static void applyFullInsets(View rootView) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
            );
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }
}
