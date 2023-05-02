package com.cominatyou.negi.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.cominatyou.negi.AccountDetailsActivity;
import com.cominatyou.negi.R;
import com.cominatyou.negi.models.TwoFactorAccount;
import com.cominatyou.negi.util.IconUtil;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Arrays;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.TwoFactorAccountViewHolder> {
    private final Context context;
    private final ArrayList<TwoFactorAccount> accounts;

    public AccountsAdapter(Context context, TwoFactorAccount[] accounts) {
        this.context = context;
        this.accounts = new ArrayList<>(Arrays.asList(accounts));
    }

    public static class TwoFactorAccountViewHolder extends RecyclerView.ViewHolder {
        private final TextView accountName;
        private final TextView accountUsername;
        private final ShapeableImageView accountIcon;

        public TwoFactorAccountViewHolder(View accountCard) {
            super(accountCard);

            accountName = accountCard.findViewById(R.id.account_name);
            accountUsername = accountCard.findViewById(R.id.account_username);
            accountIcon = accountCard.findViewById(R.id.account_icon);
        }
    }

    @NonNull
    @Override
    public TwoFactorAccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View accountCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_card, parent, false);
        return new TwoFactorAccountViewHolder(accountCard);
    }

    @Override
    public void onBindViewHolder(@NonNull TwoFactorAccountViewHolder viewHolder, int position) {
        final TwoFactorAccount account = accounts.get(position);

        viewHolder.accountName.setText(account.getName());
        viewHolder.accountUsername.setText(account.getUsername());
        if (IconUtil.hasIcon(account.getName())) {
            viewHolder.accountIcon.setImageDrawable(AppCompatResources.getDrawable(context, IconUtil.getIcon(account.getName())));
            final int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 38, context.getResources().getDisplayMetrics());

            // scale down icons to match the size of the "person" icon
            viewHolder.accountIcon.getLayoutParams().width = size;
            viewHolder.accountIcon.getLayoutParams().height = size;
            viewHolder.accountIcon.requestLayout();
        }

        viewHolder.itemView.setOnClickListener(v -> {
            final Intent launchIntent = new Intent(context, AccountDetailsActivity.class);
            launchIntent.putExtra("account", account);
            context.startActivity(launchIntent);
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public void addAccount(TwoFactorAccount account) {
        accounts.add(account);
        notifyItemInserted(accounts.size() - 1);
    }
}
