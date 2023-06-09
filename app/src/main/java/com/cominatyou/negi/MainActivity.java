package com.cominatyou.negi;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cominatyou.negi.adapters.AccountsAdapter;
import com.cominatyou.negi.data.UserAccounts;
import com.cominatyou.negi.databinding.ActivityMainBinding;
import com.cominatyou.negi.models.TwoFactorAccount;
import com.cominatyou.negi.util.SecurePrefUtil;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private AccountsAdapter adapter;
    private boolean shouldDisplayBiometricPrompt = false;

    private final ActivityResultLauncher<Intent> authLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            shouldDisplayBiometricPrompt = false;
        }
    });

    private final ActivityResultLauncher<Intent> addAccountLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() != RESULT_OK) return;
        try {
            final TwoFactorAccount[] accounts = UserAccounts.getAccounts(getApplicationContext());
            adapter.addAccount(accounts[accounts.length - 1]);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SecurePrefUtil.getBoolean(this, "app_lock_enabled", false)) {
            final Intent intent = new Intent(this, AuthActivity.class);
            authLauncher.launch(intent);
        }

        DynamicColors.applyToActivityIfAvailable(this);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        try {
            final TwoFactorAccount[] accounts = UserAccounts.getAccounts(this);
            adapter = new AccountsAdapter(this, accounts);
            binding.accountsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.accountsRecyclerView.setAdapter(adapter);
        }
        catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(binding.activityMainRootCoordinatorLayout, R.string.activity_main_account_error, Snackbar.LENGTH_LONG).show();
        }

        binding.addAccontFab.setOnClickListener(v -> addAccountLauncher.launch(new Intent(this, AddAccountActivity.class)));

        binding.mainActivityNavbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.activity_main_menu_settings) {
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        shouldDisplayBiometricPrompt = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldDisplayBiometricPrompt && SecurePrefUtil.getBoolean(this, "app_lock_enabled", false)) {
            final Intent intent = new Intent(this, AuthActivity.class);
            authLauncher.launch(intent);
        }
    }
}