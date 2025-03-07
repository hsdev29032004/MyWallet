package com.example.mywallet;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;


public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private DatabaseHelper databaseHelper;
    private TextView tvIncome, tvExpense, tvBalance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_history,container,false);

        recyclerView=view.findViewById(R.id.recyclerViewHistory);
        tvIncome=view.findViewById(R.id.tvIncome);
        tvExpense=view.findViewById(R.id.tvExpense);
        tvBalance=view.findViewById(R.id.tvBalance);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseHelper=new DatabaseHelper(getContext());
        loadTransactionHistory();
        return view;
    }
    private void loadTransactionHistory() {
        List<Map<String, String>> transactions = databaseHelper.getTransactionsForCurrentUser(getContext());
        double totalIncome = 0, totalExpense = 0;

        for (Map<String, String> transaction : transactions) {
            Log.d("TransactionDebug", "Giao dịch: " + transaction.toString());

            String amountStr = transaction.get("amount");
            String type = transaction.get("transaction_type");

            if (amountStr == null || type == null) {
                Log.e("TransactionError", "Thiếu dữ liệu: " + transaction.toString());
                continue;
            }

            double amount = Double.parseDouble(amountStr);

            if ("Thu".equals(type)) {
                totalIncome += amount;
            } else if ("Chi".equals(type)) {
                totalExpense += amount;
            } else {
                Log.e("TransactionError", "Loại giao dịch không hợp lệ: " + type);
            }
        }


        double balance = totalIncome - totalExpense;
        if (balance < 0) {
            tvBalance.setTextColor(Color.RED);  // Số dư âm màu đỏ
        } else {
            tvBalance.setTextColor(Color.GREEN); // Số dư dương màu xanh lá
        }

        tvIncome.setText("Tổng thu\n" + totalIncome + "đ");
        tvExpense.setText("Tổng chi\n" + totalExpense + "đ");
        tvBalance.setText("Số dư hiện tại: " + balance + "đ");

        adapter = new TransactionAdapter(transactions);
        recyclerView.setAdapter(adapter);
    }
}
