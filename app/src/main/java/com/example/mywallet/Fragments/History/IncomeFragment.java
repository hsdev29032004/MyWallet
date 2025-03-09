package com.example.mywallet.Fragments.History;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.Models.Transaction;
import com.example.mywallet.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IncomeFragment extends Fragment {
    private EditText edtStartDate, edtEndDate;
    private Button btnSearch;
    private ListView lvIncomeHistory;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        edtStartDate = view.findViewById(R.id.edtStartDate);
        edtEndDate = view.findViewById(R.id.edtEndDate);
        btnSearch = view.findViewById(R.id.btnSearch);
        lvIncomeHistory = view.findViewById(R.id.lvIncomeHistory);

        dbHelper = new DatabaseHelper(getContext());

        edtStartDate.setOnClickListener(v -> showDatePicker(edtStartDate));
        edtEndDate.setOnClickListener(v -> showDatePicker(edtEndDate));

        // Load tất cả thu nhập khi mở màn hình
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

        if (getContext() != null) {
            IncomeAdapter adapter = new IncomeAdapter(getContext(), incomeTransactions);
            lvIncomeHistory.setAdapter(adapter);
        }
    }

    private static class IncomeAdapter extends ArrayAdapter<Transaction> {
        public IncomeAdapter(Context context, List<Transaction> transactions) {
            super(context, R.layout.item_transaction, transactions);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_transaction, parent, false);
            }

            Transaction transaction = getItem(position);

            TextView tvCategory = convertView.findViewById(R.id.tvCategory);
            TextView tvAmount = convertView.findViewById(R.id.tvAmount);
            TextView tvNote = convertView.findViewById(R.id.tvNote);
            TextView tvDate = convertView.findViewById(R.id.tvDate);

            if (transaction != null) {
                tvCategory.setText(transaction.getCategoryName());
                tvAmount.setText(String.valueOf(transaction.getAmount()));
                tvNote.setText(transaction.getNote());
                tvDate.setText(transaction.getDate());
            }

            return convertView;
        }
    }
}
