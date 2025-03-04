package com.example.mywallet.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywallet.Models.Account;
import com.example.mywallet.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {
    private List<Account> accountList;

    public AccountAdapter(List<Account> accountList) {
        this.accountList = (accountList != null) ? accountList : new ArrayList<>();
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Account account = accountList.get(position);
        holder.txtAccountName.setText(account.getName());
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedBalance = decimalFormat.format(account.getBalance());
        holder.txtBalance.setText(formattedBalance + " VND");
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView txtAccountName, txtBalance;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAccountName = itemView.findViewById(R.id.txtAccountName);
            txtBalance = itemView.findViewById(R.id.txtBalance);
        }
    }
}
