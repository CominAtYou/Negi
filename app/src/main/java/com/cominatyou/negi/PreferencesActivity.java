package com.cominatyou.negi;

import android.app.KeyguardManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import com.cominatyou.negi.databinding.ActivityPreferencesBinding;
import com.cominatyou.negi.util.SecurePrefUtil;
import com.google.android.material.color.DynamicColors;

public class PreferencesActivity extends AppCompatActivity {
    private boolean presentAuthDialog = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);
        final var binding = ActivityPreferencesBinding.inflate(getLayoutInflater());
        final boolean appLockEnabled = SecurePrefUtil.getBoolean(this, "app_lock_enabled", false);
        setContentView(binding.getRoot());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        binding.appLockSwitch.setChecked(appLockEnabled);

        final BiometricManager biometricManager = BiometricManager.from(this);
        final int biometricStatus = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);

        if (biometricStatus == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE || biometricStatus == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED && !getSystemService(KeyguardManager.class).isDeviceSecure()) {
            binding.appLockLayout.setEnabled(false);
            binding.appLockSwitch.setEnabled(false);
            binding.appLockLayout.setAlpha(0.5f);
            binding.appLockPreferenceDescription.setText(R.string.activity_preferences_app_lock_unavailable);
        }

        binding.appLockSwitch.setOnCheckedChangeListener((self, state) -> {
            BiometricPrompt.PromptInfo.Builder promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.activity_auth_biometric_prompt_title))
                    .setSubtitle(getString(R.string.activity_preferences_biometric_auth_description))
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL);

            switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                case BiometricManager.BIOMETRIC_SUCCESS: {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        promptInfo.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL);
                    }
                    else {
                        promptInfo.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG);
                    }
                    break;
                }
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED: {
                    if (getSystemService(KeyguardManager.class).isDeviceSecure()) {
                        promptInfo.setAllowedAuthenticators(BiometricManager.Authenticators.DEVICE_CREDENTIAL);
                    }
                    else {
                        presentAuthDialog = false;
                        binding.appLockSwitch.setChecked(!state);
                        presentAuthDialog = true;
                    }
                }
                default: {
                    break;
                }
            }

            final BiometricPrompt prompt = new BiometricPrompt(this, ContextCompat.getMainExecutor(this), new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    SecurePrefUtil.putBoolean(PreferencesActivity.this, "app_lock_enabled", state);
                }

                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    presentAuthDialog = false;
                    binding.appLockSwitch.setChecked(!state);
                    presentAuthDialog = true;
                }
            });

            if (presentAuthDialog) prompt.authenticate(promptInfo.build());
        });

        binding.appLockLayout.setOnClickListener(self -> binding.appLockSwitch.toggle());
        binding.topBar.setNavigationOnClickListener(self -> finish());
    }
}
