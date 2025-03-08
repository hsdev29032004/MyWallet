package com.example.mywallet.Activities;

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
import com.example.mywallet.Models.Account;
import com.example.mywallet.Models.Budget;
import com.example.mywallet.Models.Category;
import com.example.mywallet.R;

import java.util.Calendar;
import java.util.List;

public class UpdateBudgetActivity extends AppCompatActivity {

    private ImageButton btnClose, btnSave;
    private EditText edtAmount, edBudgetName;
    private Spinner spCategory, spAccount;
    private TextView tvStartDate, tvEndDate;
    private Button btnUpdate, btnDelete;
    private DatabaseHelper dbHelper;
    private int budgetId;
    private Budget currentBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_budget);
        getViews();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBudget(); // Gọi hàm update ngân sách
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dbHelper = new DatabaseHelper(this);
        Intent intent = getIntent();        // Lấy budgetId từ Intent
        budgetId = intent.getIntExtra("BUDGET_ID", -1);
        // Nếu có budgetId hợp lệ, lấy dữ liệu ngân sách từ cơ sở dữ liệu
        if (budgetId != -1) {
            loadBudgetData(budgetId);
        }

        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartDatePicker(v); // Hiển thị DatePickerDialog khi nhấn vào TextView tvStartDate
            }
        });

        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndDatePicker(v); // Hiển thị DatePickerDialog khi nhấn vào TextView tvEndDate
            }
        });

        btnUpdate.setOnClickListener(v -> updateBudget());
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    // Hàm tải dữ liệu ngân sách từ cơ sở dữ liệu
    private void loadBudgetData(int budgetId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Truy vấn thông tin ngân sách theo budgetId
        Cursor cursor = db.query(DatabaseHelper.TABLE_BUDGET, null,
                DatabaseHelper.COLUMN_BUDGET_ID + " = ?", new String[]{String.valueOf(budgetId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Lấy dữ liệu từ cursor
            double amountLimit = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT_LIMIT));
            String budgetName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_NAME));
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID));
            int accountId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCOUNT_ID));
            String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_START_DATE));
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_END_DATE));

            // Đổ dữ liệu vào các trường
            edtAmount.setText(String.valueOf(amountLimit));
            edBudgetName.setText(budgetName);

            // Cài đặt Spinner (Danh mục và Tài khoản) - Bạn cần lấy danh sách từ cơ sở dữ liệu
            setCategorySpinner(categoryId);
            setAccountSpinner(accountId);

            // Cài đặt ngày bắt đầu và ngày kết thúc
            tvStartDate.setText(startDate);
            tvEndDate.setText(endDate);

            cursor.close();
        }
    }

    // Hàm thiết lập Spinner cho danh mục
    private void setCategorySpinner(int categoryId) {
        // Lấy danh mục từ cơ sở dữ liệu và thiết lập vào Spinner
        List<Category> categories = dbHelper.getAllCategories_NonDeleted();
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        // Cài đặt giá trị mặc định cho Spinner
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == categoryId) {
                spCategory.setSelection(i);
                break;
            }
        }
    }

    // Hàm thiết lập Spinner cho tài khoản
    private void setAccountSpinner(int accountId) {
        // Lấy danh sách tài khoản từ cơ sở dữ liệu và thiết lập vào Spinner
        List<Account> accounts = dbHelper.getAllAccounts_NonDeleted();
        ArrayAdapter<Account> accountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accounts);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAccount.setAdapter(accountAdapter);
        // Cài đặt giá trị mặc định cho Spinner
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getId() == accountId) {
                spAccount.setSelection(i);
                break;
            }
        }
    }

    // Hàm cập nhật ngân sách
    private void updateBudget() {
        String budgetName = edBudgetName.getText().toString();
        double amountLimit = Double.parseDouble(edtAmount.getText().toString());
        int categoryId = ((Category) spCategory.getSelectedItem()).getId();
        int accountId = ((Account) spAccount.getSelectedItem()).getId();
        String startDate = tvStartDate.getText().toString();
        String endDate = tvEndDate.getText().toString();

        // Cập nhật dữ liệu ngân sách vào cơ sở dữ liệu
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

    // Hàm hiển thị hộp thoại xác nhận xóa
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Bạn có chắc chắn muốn xóa ngân sách này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteBudget())
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Hàm xóa ngân sách
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

    // Phương thức để hiển thị DatePickerDialog cho ngày bắt đầu
    public void showStartDatePicker(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, selectedYear, selectedMonth, selectedDay) -> {
            // Hiển thị ngày được chọn
            tvStartDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
        }, year, month, day);

        datePickerDialog.show();
    }

    // Phương thức để hiển thị DatePickerDialog cho ngày kết thúc
    public void showEndDatePicker(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, selectedYear, selectedMonth, selectedDay) -> {
            // Hiển thị ngày được chọn
            tvEndDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
        }, year, month, day);

        datePickerDialog.show();
    }

    public void getViews(){
        btnSave = findViewById(R.id.btnSave);
        btnClose = findViewById(R.id.btnClose);
        edtAmount = findViewById(R.id.edtAmount);
        edBudgetName = findViewById(R.id.edBudgetName);
        spCategory = findViewById(R.id.spCategory);
        spAccount = findViewById(R.id.spAccount);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnXoa);
    }
}