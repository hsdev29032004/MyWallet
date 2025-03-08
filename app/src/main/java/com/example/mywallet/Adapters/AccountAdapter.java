package com.example.mywallet.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywallet.Activities.Account.EditAccountActivity;
import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.Models.Account;
import com.example.mywallet.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {
    private List<Account> accountList;
    private Context context;
    private DatabaseHelper dbHelper;

    public AccountAdapter(Context context, List<Account> accountList) {
        this.context = context;
        this.accountList = (accountList != null) ? accountList : new ArrayList<>();
        this.dbHelper = new DatabaseHelper(context);
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

        // Format số dư
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedBalance = decimalFormat.format(account.getBalance());
        holder.txtBalance.setText(String.format("%,.0f VND", account.getBalance()));

        // Xử lý sự kiện xóa
        holder.btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog(account, position));

        // Xử lý sự kiện chỉnh sửa
        holder.btnEdit.setOnClickListener(v -> openEditAccountActivity(account));
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public void updateData(List<Account> accountList) {
        this.accountList = accountList;
        notifyDataSetChanged();
    }

    private void showDeleteConfirmationDialog(Account account, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản \"" + account.getName() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteAccount(account, position))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteAccount(Account account, int position) {
        boolean deleted = dbHelper.deleteAccount(account.getId());
        if (deleted) {
            accountList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Xóa tài khoản thành công!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Xóa tài khoản thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void openEditAccountActivity(Account account) {
        Intent intent = new Intent(context, EditAccountActivity.class);
        intent.putExtra("ACCOUNT_ID", account.getId());
        intent.putExtra("ACCOUNT_NAME", account.getName());
        intent.putExtra("ACCOUNT_BALANCE", String.valueOf(account.getBalance()));
        context.startActivity(intent);
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView txtAccountName, txtBalance;
        ImageButton btnDelete, btnEdit;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAccountName = itemView.findViewById(R.id.txtAccountName);
            txtBalance = itemView.findViewById(R.id.txtBalance);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEditAccount);
        }
    }
}
