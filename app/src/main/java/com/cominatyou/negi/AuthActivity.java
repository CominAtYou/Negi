package com.cominatyou.negi;

import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.cominatyou.negi.databinding.ActivityAuthBinding;
import com.cominatyou.negi.util.SecurePrefUtil;
import com.google.android.material.color.DynamicColors;

public class AuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);
        final ActivityAuthBinding binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final BiometricPrompt.PromptInfo.Builder promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.activity_auth_biometric_prompt_title))
                .setSubtitle(getString(R.string.activity_auth_biometric_prompt_subtitle))
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL);

        final BiometricManager biometricManager = BiometricManager.from(this);
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
                if (!getSystemService(KeyguardManager.class).isDeviceSecure()) {
                    // no keyguard is set; app lock is no more
                    SecurePrefUtil.putBoolean(this, "app_lock_enabled", false);
                    Toast.makeText(this, R.string.activity_auth_keyguard_removed_toast_text, Toast.LENGTH_LONG).show();
                    finish();
                    break;
                }
                else {
                    // fall back to PIN
                    promptInfo.setAllowedAuthenticators(BiometricManager.Authenticators.DEVICE_CREDENTIAL);
                }

                break;
            }
            default: {
                break;
            }
        }

        final BiometricPrompt prompt = new BiometricPrompt(this, ContextCompat.getMainExecutor(this), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                setResult(RESULT_OK);
                finish();
            }
        });

        binding.activityAuthUnlockButton.setOnClickListener(v -> prompt.authenticate(promptInfo.build()));
        prompt.authenticate(promptInfo.build());
    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
