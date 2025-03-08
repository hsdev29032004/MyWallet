package com.example.mywallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;


import android.view.LayoutInflater;
import android.view.View;
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Map<String, String>> transactionList;
    private HistoryFragment fragment;
    private DatabaseHelper databaseHelper;
    public TransactionAdapter(List<Map<String, String>> transactionList, DatabaseHelper databaseHelper, HistoryFragment fragment) {
        this.transactionList = transactionList;
        this.databaseHelper = databaseHelper;
        this.fragment = fragment;
    }
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction,parent,false);
       return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> transaction = transactionList.get(position);

        holder.tvCategoryName.setText(transaction.get("category_name")); // Hiển thị tên danh mục
        holder.tvDate.setText(transaction.get("date"));
        holder.tvAmount.setText(transaction.get("amount") + " VND");
        holder.tvEdit.setOnClickListener(v -> {
            String transactionIdStr = transaction.get("transaction_id");
            if (transactionIdStr == null || transactionIdStr.isEmpty()) {
                Log.e("EditTransaction", "transaction_id bị null hoặc rỗng!");
                return;
            }

            try {
                int transactionId = Integer.parseInt(transactionIdStr);
                Log.d("EditTransaction", "Nhấn vào chỉnh sửa, transaction_id: " + transactionId);

                // Tạo Intent để mở trang chỉnh sửa giao dịch
                Context context = v.getContext();
                Intent intent = new Intent(context, Transaction.class);
                intent.putExtra("transaction_id", transactionId);

                // Kiểm tra trước khi mở activity
                if (context instanceof Activity) {
                    ((Activity) context).startActivity(intent);
                } else {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            } catch (NumberFormatException e) {
                Log.e("EditTransaction", "Lỗi chuyển đổi transaction_id: " + e.getMessage());
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Xóa giao dịch")
                    .setMessage("Bạn có chắc muốn xóa giao dịch này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        try {
                            int transactionId = Integer.parseInt(transaction.get("transaction_id"));

                            // Xóa giao dịch trong database
                            databaseHelper.deleteTransaction(transactionId);

                            // Xóa khỏi danh sách hiển thị
                            transactionList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, transactionList.size());

                            // Cập nhật lại tổng thu, tổng chi, số dư
                            fragment.loadTransactionHistory();
                        } catch (NumberFormatException e) {
                            Log.e("DeleteTransaction", "Lỗi chuyển đổi transaction_id: " + e.getMessage());
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            return true;
        });




        // Đổi màu tiền thu/chi
        double amount = Double.parseDouble(transaction.get("amount"));
        String type = transaction.get("transaction_type").trim().toLowerCase();
        if ("Thu".equalsIgnoreCase(type)) {  // Dùng equalsIgnoreCase để so sánh không phân biệt hoa thường
            holder.tvAmount.setTextColor(Color.GREEN);
        } else if ("Chi".equalsIgnoreCase(type)) {
            holder.tvAmount.setTextColor(Color.RED);
        } else {
            holder.tvAmount.setTextColor(Color.BLACK); // Màu mặc định nếu có lỗi
        }
    }

    public int getItemCount(){
        return transactionList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvCategoryName, tvDate, tvAmount,tvEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvEdit = itemView.findViewById(R.id.tvEdit);
        }

    }

}
