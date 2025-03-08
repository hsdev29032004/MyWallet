package com.example.mywallet;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Transaction extends AppCompatActivity {

    private Spinner spinnerAccount, spinnerCategory;
    private EditText edtAmount, edtDueDate, edtNote;
    private TextView txtDate;
    private Button btnAddTransaction;
    private DatabaseHelper db;
    private int userId;
    private List<String> accountList;
    private List<String> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        // Ánh xạ UI
        spinnerAccount = findViewById(R.id.spinnerAccount);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        edtAmount = findViewById(R.id.edtAmount);
        txtDate = findViewById(R.id.txtDate);
        edtDueDate = findViewById(R.id.edtDueDate);
        edtNote = findViewById(R.id.edtNote);
        btnAddTransaction = findViewById(R.id.btnAddTransaction);
        db = new DatabaseHelper(this);

        // Lấy user_id từ Shared Preferences
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy user_id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị danh sách tài khoản & danh mục
        loadAccounts();
        loadCategories();

        // Lấy ngày hiện tại
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        txtDate.setText("Ngày: " + currentDate);

        // Sự kiện chọn danh mục để hiển thị due_date nếu cần
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoryType = categoryList.get(position); // Loại danh mục được chọn
                if (categoryType.equals("Vay") || categoryType.equals("Khoản cho vay")) {
                    edtDueDate.setVisibility(View.VISIBLE);
                } else {
                    edtDueDate.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Xử lý khi nhấn nút tạo giao dịch
        btnAddTransaction.setOnClickListener(view -> saveTransaction());
    }

    private void loadAccounts() {
        accountList = db.getAccountsByUser(userId); // Danh sách tên tài khoản
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, accountList);
        spinnerAccount.setAdapter(adapter);
    }

    private void loadCategories() {
        categoryList = db.getCategories(); // Danh sách tên danh mục
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryList);
        spinnerCategory.setAdapter(adapter);
    }

    private void saveTransaction() {
        // Lấy tên tài khoản và danh mục, sau đó tìm account_id và category_id
        String selectedAccountName = accountList.get(spinnerAccount.getSelectedItemPosition());
        String selectedCategoryName = categoryList.get(spinnerCategory.getSelectedItemPosition());

        // Tìm account_id và category_id tương ứng
        int selectedAccountId = db.getAccountIdByName(selectedAccountName);
        int selectedCategoryId = db.getCategoryIdByName(selectedCategoryName);

        // Kiểm tra nếu user nhập số tiền hợp lệ
        double amount = 0;
        try {
            amount = Double.parseDouble(edtAmount.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = edtNote.getText().toString().isEmpty() ? "Không có ghi chú" : edtNote.getText().toString();
        String dueDate = edtDueDate.getVisibility() == View.VISIBLE ? edtDueDate.getText().toString() : null;

        // Kiểm tra số tiền không vượt ngân sách
        double remainingBudget = db.getRemainingBudget(userId, selectedCategoryId); // Sử dụng category_id
        if (amount > remainingBudget) {
            Toast.makeText(this, "Số tiền vượt quá ngân sách!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thêm vào database
        boolean success = db.insertTransaction(userId, selectedAccountId, selectedCategoryId, amount, dueDate, note);
        if (success) {
            Toast.makeText(this, "Giao dịch đã được thêm!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi thêm giao dịch!", Toast.LENGTH_SHORT).show();
        }
    }
}
