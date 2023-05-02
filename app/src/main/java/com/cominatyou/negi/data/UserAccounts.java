package com.cominatyou.negi.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.cominatyou.negi.models.TwoFactorAccount;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class UserAccounts {
    /**
     * Get the user's two factor accounts.
     * @param context System context.
     * @return Array of two factor accounts.
     * @throws GeneralSecurityException If there is an error during encryption.
     * @throws IOException If there is an error during file IO.
     */
    public static TwoFactorAccount[] getAccounts(Context context) throws GeneralSecurityException, IOException {
        final MasterKey masterKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();

        final File accountsFile = new File(context.getFilesDir(), "accounts_data");

        final SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                context,
                "account_data",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        return new Gson().fromJson(sharedPreferences.getString("saved_accounts", "[]"), TwoFactorAccount[].class);
    }

    /**
     * Add a two factor account to the user's accounts.
     * @param context System context.
     * @param account The account to add.
     * @throws GeneralSecurityException If the master key is unable to be created.
     * @throws IOException If there is an error during file IO.
     */
    public static void addAccount(Context context, TwoFactorAccount account) throws GeneralSecurityException, IOException {
        final MasterKey masterKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();

        final SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                context,
                "account_data",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        final String accountsData = sharedPreferences.getString("saved_accounts", "[]");

        final TwoFactorAccount[] accounts = new Gson().fromJson(accountsData, TwoFactorAccount[].class);
        final TwoFactorAccount[] newAccounts = new TwoFactorAccount[accounts.length + 1];

        System.arraycopy(accounts, 0, newAccounts, 0, accounts.length);

        newAccounts[accounts.length] = account;

        sharedPreferences.edit().putString("saved_accounts", new Gson().toJson(newAccounts)).apply();
    }
}
