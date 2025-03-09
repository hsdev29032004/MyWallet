package com.example.mywallet.Fragments.History;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mywallet.Activities.TransactionPackage.EditTransaction;
import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.Models.Transaction;
import com.example.mywallet.R;

import java.util.Calendar;
import java.util.List;
import androidx.appcompat.app.AlertDialog;

public class IncomeFragment extends Fragment {
    private EditText edtStartDate, edtEndDate;
    private Button btnSearch;
    private ListView lvIncomeHistory;
    private DatabaseHelper dbHelper;
    private ActivityResultLauncher<Intent> editTransactionLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Chỉ cần inflate view ở đây
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

        // Khởi tạo ActivityResultLauncher trong onCreateView
        editTransactionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Tải lại giao dịch sau khi quay lại từ EditTransaction
                        loadIncomeTransactions("", "");
                    }
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

    // Xóa từ khóa static
    private class IncomeAdapter extends ArrayAdapter<Transaction> {
        private List<Transaction> transactions;
        private DatabaseHelper dbHelper;

        public IncomeAdapter(Context context, List<Transaction> transactions) {
            super(context, R.layout.item_transaction, transactions);
            this.transactions = transactions;
            this.dbHelper = new DatabaseHelper(context);
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
            ImageButton btnEdit = convertView.findViewById(R.id.btnEdit);
            ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);

            if (transaction != null) {
                tvCategory.setText(transaction.getCategoryName());
                tvAmount.setText(String.valueOf(transaction.getAmount()));
                tvNote.setText(transaction.getNote());
                tvDate.setText(transaction.getDate());

                // Xử lý sự kiện sửa
                btnEdit.setOnClickListener(v -> {
                    if (transaction != null) {
                        Log.d("IncomeFragment", "Transaction ID: " + transaction.getTransactionId());
                        Intent intent = new Intent(getContext(), EditTransaction.class);
                        intent.putExtra("transaction_id", transaction.getTransactionId());

                        // Gọi launcher thay vì startActivityForResult
                        editTransactionLauncher.launch(intent);
                    } else {
                        Log.d("IncomeFragment", "Transaction is null");
                    }
                });

                // Xử lý sự kiện xóa
                btnDelete.setOnClickListener(v -> {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Xóa giao dịch")
                            .setMessage("Bạn có chắc chắn muốn xóa giao dịch này?")
                            .setPositiveButton("Xóa", (dialog, which) -> {
                                dbHelper.deleteTransaction(transaction.getTransactionId());
                                transactions.remove(position);
                                notifyDataSetChanged();
                            })
                            .setNegativeButton("Hủy", null)
                            .show();
                });
            }

            return convertView;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tải lại giao dịch thu nhập khi quay lại fragment
        loadIncomeTransactions("", "");
    }
}
