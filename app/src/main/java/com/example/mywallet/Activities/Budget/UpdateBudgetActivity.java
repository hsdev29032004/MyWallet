package com.example.mywallet.Activities.Budget;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.Models.Category;
import com.example.mywallet.R;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UpdateBudgetActivity extends AppCompatActivity {

    private ImageButton btnClose, btnSave;
    private EditText edtAmount, edBudgetName;
    private Spinner spCategory;
    private TextView tvStartDate, tvEndDate;
    private Button btnUpdate, btnDelete;
    private DatabaseHelper dbHelper;
    private int budgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_budget);
        getViews();

        dbHelper = new DatabaseHelper(this);
        Intent intent = getIntent();
        budgetId = intent.getIntExtra("BUDGET_ID", -1);

        if (budgetId != -1) {
            loadBudgetData(budgetId);
        }

        btnSave.setOnClickListener(v -> updateBudget());
        btnClose.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> updateBudget());
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

        tvStartDate.setOnClickListener(this::showStartDatePicker);
        tvEndDate.setOnClickListener(this::showEndDatePicker);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadBudgetData(int budgetId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_BUDGET, null,
                DatabaseHelper.COLUMN_BUDGET_ID + " = ?", new String[]{String.valueOf(budgetId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            double amountLimit = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT_LIMIT));
            String budgetName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_NAME));
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID));
            String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_START_DATE));
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_END_DATE));

            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            edtAmount.setText(formatter.format(amountLimit));
            edBudgetName.setText(budgetName);
            setCategorySpinner(categoryId);
            tvStartDate.setText(startDate);
            tvEndDate.setText(endDate);

            cursor.close();
        }
    }

    private void setCategorySpinner(int categoryId) {
        List<Category> categories = dbHelper.getAllExpenseCategories_NonDeleted();
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == categoryId) {
                spCategory.setSelection(i);
                break;
            }
        }
    }

    private void updateBudget() {
        String budgetName = edBudgetName.getText().toString().trim();
        String amountText = edtAmount.getText().toString().trim();

        if (budgetName.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Xóa dấu chấm ngăn cách hàng nghìn và thay dấu phẩy thành dấu chấm
        amountText = amountText.replace(".", "").replace(",", ".");

        double amountLimit;
        try {
            amountLimit = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        int categoryId = ((Category) spCategory.getSelectedItem()).getId();
        String startDate = tvStartDate.getText().toString();
        String endDate = tvEndDate.getText().toString();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_BUDGET_NAME, budgetName);
        values.put(DatabaseHelper.COLUMN_AMOUNT_LIMIT, amountLimit);
        values.put(DatabaseHelper.COLUMN_CATEGORY_ID, categoryId);
        values.put(DatabaseHelper.COLUMN_START_DATE, startDate);
        values.put(DatabaseHelper.COLUMN_END_DATE, endDate);

        int rowsAffected = db.update(DatabaseHelper.TABLE_BUDGET, values,
                DatabaseHelper.COLUMN_BUDGET_ID + " = ?", new String[]{String.valueOf(budgetId)});

        if (rowsAffected > 0) {
            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Bạn có chắc chắn muốn xóa ngân sách này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteBudget())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteBudget() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(DatabaseHelper.TABLE_BUDGET,
                DatabaseHelper.COLUMN_BUDGET_ID + " = ?", new String[]{String.valueOf(budgetId)});

        if (rowsAffected > 0) {
            Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    public void showStartDatePicker(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (datePicker, selectedYear, selectedMonth, selectedDay) ->
                tvStartDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear),
                year, month, day).show();
    }

    public void showEndDatePicker(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (datePicker, selectedYear, selectedMonth, selectedDay) ->
                tvEndDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear),
                year, month, day).show();
    }

    public void getViews() {
        btnSave = findViewById(R.id.btnSave);
        btnClose = findViewById(R.id.btnClose);
        edtAmount = findViewById(R.id.edtAmount);
        edBudgetName = findViewById(R.id.edBudgetName);
        spCategory = findViewById(R.id.spCategory);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnXoa);
    }
}
