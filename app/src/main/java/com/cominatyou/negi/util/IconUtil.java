package com.cominatyou.negi.util;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import com.cominatyou.negi.R;

import java.util.Map;

public class IconUtil {
    private static Map<String, Integer> iconMap = Map.ofEntries(
            Map.entry("Google", R.drawable.google_logo),
            Map.entry("Gmail", R.drawable.google_logo)
    );

    public static boolean hasIcon(String accountName) {
        return iconMap.containsKey(accountName);
    }

    @DrawableRes
    public static int getIcon(String accountName) {
        return iconMap.get(accountName);
    }
}
