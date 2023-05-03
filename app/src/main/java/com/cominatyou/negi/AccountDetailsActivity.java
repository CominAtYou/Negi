package com.cominatyou.negi;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.cominatyou.negi.databinding.ActivityAccountDetailsBinding;
import com.cominatyou.negi.models.TwoFactorAccount;
import com.cominatyou.negi.util.IconUtil;
import com.google.android.material.color.DynamicColors;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import de.taimos.totp.TOTP;

public class AccountDetailsActivity extends AppCompatActivity {
    private ActivityAccountDetailsBinding binding;
    private TwoFactorAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);
        binding = ActivityAccountDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            account = getIntent().getParcelableExtra("account", TwoFactorAccount.class);
        }
        else {
            account = (TwoFactorAccount) getIntent().getSerializableExtra("account");
        }
        assert account != null;

        binding.accountDetailsCollapsingLayout.setTitle(account.getName());
        binding.accountUsername.setText(account.getUsername());
        binding.otpTextView.setText(TOTP.getOTP(account.getHexEncodedSecret()));

        if (IconUtil.hasIcon(account.getName())) {
            final int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
            binding.accountIconImageView.getLayoutParams().width = size;
            binding.accountIconImageView.getLayoutParams().height = size;

            binding.accountIconImageView.setImageDrawable(AppCompatResources.getDrawable(this, IconUtil.getIcon(account.getName())));
            binding.accountIconImageView.requestLayout();
        }

        binding.activityAccountDetailsToolbar.setNavigationOnClickListener(v -> finish());

        final Calendar now = Calendar.getInstance(TimeZone.getDefault());
        final SimpleDateFormat sdf = new SimpleDateFormat("ss", Locale.getDefault());
        final int secondsToRefresh = 30 - Integer.parseInt(sdf.format(now.getTime())) % 30;
        binding.codeRefreshCountdownTextView.setText(String.valueOf(secondsToRefresh));

        new CountDownTimer(secondsToRefresh * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.codeRefreshCountdownTextView.setText(String.valueOf((int) Math.floor(millisUntilFinished / 1000f)));
            }

            @Override
            public void onFinish() {
                binding.otpTextView.setText(TOTP.getOTP(account.getHexEncodedSecret()));
                new CountDownTimer(30000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        binding.codeRefreshCountdownTextView.setText(String.valueOf((int) Math.floor(millisUntilFinished / 1000f)));
                    }

                    public void onFinish() {
                        binding.otpTextView.setText(TOTP.getOTP(account.getHexEncodedSecret()));
                        start();
                    }
                }.start();
            }
        }.start();
    }
}
