package com.example.mywallet.Fragments.History;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.Models.Transaction;
import com.example.mywallet.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncomeFragment extends Fragment {
    private EditText edtStartDate, edtEndDate;
    private Button btnSearch;
    private LinearLayout layoutIncomeHistory;
    private PieChart pieChartIncome;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        edtStartDate = view.findViewById(R.id.edtStartDate);
        edtEndDate = view.findViewById(R.id.edtEndDate);
        btnSearch = view.findViewById(R.id.btnSearch);
        layoutIncomeHistory = view.findViewById(R.id.layoutIncomeHistory);
        pieChartIncome = view.findViewById(R.id.pieChartIncome);

        dbHelper = new DatabaseHelper(getContext());

        edtStartDate.setOnClickListener(v -> showDatePicker(edtStartDate));
        edtEndDate.setOnClickListener(v -> showDatePicker(edtEndDate));

        loadIncomeTransactions("", "");

        btnSearch.setOnClickListener(v -> {
            String startDate = edtStartDate.getText().toString();
            String endDate = edtEndDate.getText().toString();
            loadIncomeTransactions(startDate, endDate);
        });

        return view;
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String date = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    editText.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void loadIncomeTransactions(String startDate, String endDate) {
        List<Transaction> incomeTransactions = dbHelper.getIncomeTransactions(startDate, endDate);
        layoutIncomeHistory.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (Transaction transaction : incomeTransactions) {
            View itemView = inflater.inflate(R.layout.item_transaction, layoutIncomeHistory, false);

            TextView tvCategory = itemView.findViewById(R.id.tvCategory);
            TextView tvAmount = itemView.findViewById(R.id.tvAmount);
            TextView tvNote = itemView.findViewById(R.id.tvNote);
            TextView tvDate = itemView.findViewById(R.id.tvDate);

            tvCategory.setText(transaction.getCategoryName());
            tvAmount.setText(String.valueOf(transaction.getAmount()));
            tvNote.setText(transaction.getNote());
            tvDate.setText(transaction.getDate());

            layoutIncomeHistory.addView(itemView);
        }
        loadIncomeChart(incomeTransactions);
    }

    private void loadIncomeChart(List<Transaction> transactions) {
        Map<String, Double> categoryTotals = new HashMap<>();

        for (Transaction transaction : transactions) {
            String categoryName = transaction.getCategoryName();
            double amount = transaction.getAmount();

            categoryTotals.put(categoryName, categoryTotals.getOrDefault(categoryName, 0.0) + amount);
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Thu nhập");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(14f);

        PieData pieData = new PieData(dataSet);

        pieChartIncome.setData(pieData);
        pieChartIncome.setCenterText("Thu nhập theo danh mục");
        pieChartIncome.animateY(1000);
    }
}