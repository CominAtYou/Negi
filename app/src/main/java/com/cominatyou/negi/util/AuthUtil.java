package com.cominatyou.negi.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class AuthUtil {
    public static boolean isAppLockEnabled(Context context) {
        try {
            final MasterKey masterKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();

            final SharedPreferences preferences = EncryptedSharedPreferences.create(
                    context,
                    "secure_preferences",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            return preferences.getBoolean("app_lock_enabled", false);
        }
        catch (Exception e) {
            return false;
        }
    }
}
