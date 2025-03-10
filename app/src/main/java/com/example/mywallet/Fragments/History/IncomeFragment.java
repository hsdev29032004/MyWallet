package com.example.mywallet.Fragments.History;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
            ImageButton btnDelete = itemView.findViewById(R.id.btnDelete); // Nút xóa
            ImageButton btnEdit = itemView.findViewById(R.id.btnEdit);

            tvCategory.setText(transaction.getCategoryName());
            tvAmount.setText(String.valueOf(transaction.getAmount()));
            tvNote.setText(transaction.getNote());
            tvDate.setText(transaction.getDate());

            // Xử lý sự kiện khi nhấn nút xóa
            btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog(transaction.getTransactionId(), startDate, endDate));
            // Xử lý sự kiện sửa
            btnEdit.setOnClickListener(v -> {
                showEditTransactionDialog(transaction, startDate, endDate);
            });


            layoutIncomeHistory.addView(itemView);
        }
        loadIncomeChart(incomeTransactions);
        // Cập nhật UI ngay trên main thread
        getActivity().runOnUiThread(() -> layoutIncomeHistory.invalidate());
    }
    private void showEditTransactionDialog(Transaction transaction, String startDate, String endDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chỉnh sửa giao dịch");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_transaction, null);
        EditText edtAmount = view.findViewById(R.id.edtAmount);
        EditText edtNote = view.findViewById(R.id.edtNote);
        EditText edtDate = view.findViewById(R.id.edtDate);
        Spinner spnCategory = view.findViewById(R.id.spnCategory);

        edtAmount.setText(String.valueOf(transaction.getAmount()));
        edtNote.setText(transaction.getNote());
        edtDate.setText(transaction.getDate());

        // Lấy danh sách danh mục từ database
        Map<String, Integer> categoriesMap = dbHelper.getAllCategoriesWithIds();
        List<String> categoryNames = new ArrayList<>(categoriesMap.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, categoryNames);
        spnCategory.setAdapter(adapter);

        // Chọn danh mục hiện tại
        int categoryIndex = categoryNames.indexOf(transaction.getCategoryName());
        if (categoryIndex != -1) {
            spnCategory.setSelection(categoryIndex);
        }

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            try {
                double newAmount = Double.parseDouble(edtAmount.getText().toString());
                String newNote = edtNote.getText().toString();
                String selectedCategory = spnCategory.getSelectedItem().toString();
                int categoryId = categoriesMap.get(selectedCategory); // Lấy ID danh mục

                // Cập nhật vào database và kiểm tra kết quả
                boolean isUpdated = dbHelper.updateTransaction(transaction.getTransactionId(), newAmount, newNote, categoryId);

                if (isUpdated) {
                    // Nếu sửa thành công, cập nhật ngay trên giao diện
                    transaction.setAmount(newAmount);
                    transaction.setNote(newNote);
                    transaction.setCategoryName(selectedCategory);

                    // Cập nhật lại mục giao dịch trong layout
                    updateTransactionView(transaction);

                    // Hiển thị thông báo thành công
                    Toast.makeText(getContext(), "Cập nhật giao dịch thành công", Toast.LENGTH_SHORT).show();

                    // Làm mới lại danh sách giao dịch (cập nhật UI)
                    loadIncomeTransactions("", "");
                } else {
                    // Nếu có lỗi xảy ra, thông báo lỗi
                    Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // Bắt lỗi nếu có và hiển thị thông báo
                e.printStackTrace();
                Toast.makeText(getContext(), "Đã xảy ra lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });



        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void updateTransactionView(Transaction transaction) {
        for (int i = 0; i < layoutIncomeHistory.getChildCount(); i++) {
            View itemView = layoutIncomeHistory.getChildAt(i);
            TextView tvCategory = itemView.findViewById(R.id.tvCategory);
            TextView tvAmount = itemView.findViewById(R.id.tvAmount);
            TextView tvNote = itemView.findViewById(R.id.tvNote);

            // Kiểm tra nếu đây là giao dịch đang sửa và cập nhật lại
            if (tvCategory.getText().toString().equals(transaction.getCategoryName()) &&
                    tvAmount.getText().toString().equals(String.valueOf(transaction.getAmount())) &&
                    transaction.getTransactionId() == Integer.parseInt(tvCategory.getTag().toString())) {
                tvCategory.setText(transaction.getCategoryName());
                tvAmount.setText(String.valueOf(transaction.getAmount()));
                tvNote.setText(transaction.getNote());
                break;
            }
        }
    }


    private void showDeleteConfirmationDialog(int transactionId, String startDate, String endDate) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa giao dịch này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbHelper.deleteTransaction(transactionId);
                    loadIncomeTransactions(startDate, endDate); // Cập nhật danh sách
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
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