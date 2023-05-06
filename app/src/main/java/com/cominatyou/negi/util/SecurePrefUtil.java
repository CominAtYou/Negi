package com.cominatyou.negi.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class SecurePrefUtil {
    /**
     * Get a boolean from the encrypted shared preferences.
     * @param context System context.
     * @param key The key to get.
     * @param defValue The default value to return if the key does not exist.
     * @return A pair containing whether or not the operation was successful and the value. If the operation was not successful, {@code defValue} will be returned as the value.
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        try {
            final SharedPreferences preferences = EncryptedSharedPreferences.create(
                    context,
                    "secure_preferences",
                    new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            return preferences.getBoolean(key, defValue);
        }
        catch (Exception e) {
            return defValue;
        }
    }

    /**
     * Put a boolean into the encrypted shared preferences.
     * @param context System context.
     * @param key The key to put.
     * @param value The value to put.
     */
    public static void putBoolean(Context context, String key, boolean value) {
        try {
            final SharedPreferences preferences = EncryptedSharedPreferences.create(
                    context,
                    "secure_preferences",
                    new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            preferences.edit().putBoolean(key, value).apply();
        }
        catch (Exception ignored) {}
    }
}
