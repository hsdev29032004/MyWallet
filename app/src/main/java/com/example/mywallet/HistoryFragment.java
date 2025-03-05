package com.example.mywallet;


import android.os.Bundle;
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
            double amount = Double.parseDouble(transaction.get("amount"));
            if ("income".equals(transaction.get("transaction_type"))) {
                totalIncome += amount;
            } else {
                totalExpense += amount;
            }
        }

        double balance = totalIncome - totalExpense;

        tvIncome.setText("Tổng thu\n" + totalIncome + "đ");
        tvExpense.setText("Tổng chi\n" + totalExpense + "đ");
        tvBalance.setText("Số dư hiện tại: " + balance + "đ");

        adapter = new TransactionAdapter(transactions);
        recyclerView.setAdapter(adapter);
    }
}
