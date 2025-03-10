package com.example.mywallet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mywallet.Models.Transaction;
import com.example.mywallet.R;

import java.util.List;

public class TransactionAdapter extends BaseAdapter {
    private Context context;
    private List<Transaction> transactions;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int position) {
        return transactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        }

        Transaction transaction = transactions.get(position);

        TextView tvCategory = convertView.findViewById(R.id.tvCategory);
        TextView tvAmount = convertView.findViewById(R.id.tvAmount);
        TextView tvDate = convertView.findViewById(R.id.tvDate);
        TextView tvNote = convertView.findViewById(R.id.tvNote);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);



        tvCategory.setText(transaction.getCategoryName());
        tvAmount.setText(String.format("%,.2f VNĐ", transaction.getAmount()));
        tvDate.setText(transaction.getDate());
        tvNote.setText(transaction.getNote());

        // Xử lý sự kiện khi nhấn nút xóa
        // btnDelete.setOnClickListener(v -> {
            //transactions.remove(position); // Xóa giao dịch khỏi danh sách
            // notifyDataSetChanged(); // Cập nhật lại giao diện
        //});

        return convertView;
    }
}