package com.example.mywallet;

import android.graphics.Color;
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
    public TransactionAdapter(List<Map<String, String>> transactionList){
        this.transactionList=transactionList;
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

        // Đổi màu tiền thu/chi
        double amount = Double.parseDouble(transaction.get("amount"));
        if ("income".equals(transaction.get("transaction_type"))) {
            holder.tvAmount.setTextColor(Color.GREEN);
        } else {
            holder.tvAmount.setTextColor(Color.RED);
        }
    }

    public int getItemCount(){
        return transactionList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvCategoryName, tvDate, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
