package com.cominatyou.negi;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.cominatyou.negi.data.UserAccounts;
import com.cominatyou.negi.databinding.ActivityAddAccountBinding;
import com.cominatyou.negi.models.TwoFactorAccount;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.snackbar.Snackbar;

public class AddAccountActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);
        final ActivityAddAccountBinding binding = ActivityAddAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!binding.addAccountNameEditText.getText().toString().isBlank()
                    && !binding.addAccountUsernameEditText.getText().toString().isBlank()
                    && !binding.addAccountSecretEditText.getText().toString().isBlank()) {
                    binding.addAccountDoneButton.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        };

        binding.addAccountNameEditText.addTextChangedListener(watcher);
        binding.addAccountUsernameEditText.addTextChangedListener(watcher);
        binding.addAccountSecretEditText.addTextChangedListener(watcher);

        binding.addAccountDoneButton.setOnClickListener(v -> {
            final TwoFactorAccount newAccount = new TwoFactorAccount(
                    binding.addAccountNameEditText.getText().toString(),
                    binding.addAccountUsernameEditText.getText().toString(),
                    binding.addAccountSecretEditText.getText().toString(),
                    String.valueOf(System.currentTimeMillis())
            );

            try {
                UserAccounts.addAccount(this, newAccount);
                setResult(RESULT_OK);
                finish();
            }
            catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(binding.getRoot(), R.string.activity_add_account_error, Snackbar.LENGTH_LONG).show();
            }
        });

        binding.activityAddAccountToolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}
