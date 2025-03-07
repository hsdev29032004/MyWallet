package com.example.mywallet;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;


import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Transaction extends AppCompatActivity {


    private DatabaseHelper dbHelper;
    private EditText edtAmount;
    private EditText edtDueDate;
    private EditText edtNote;
    private Spinner spnCategory, spnPaymentMethod, spnTransactionType;
    private Button btnCreate, btnDueDate;
    private String selectedCategory, selectedPaymentMethod, selectedDate;
    private TextView tvCreatedDate;
    private String transactionType;
    private int transactionId;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        dbHelper = new DatabaseHelper(this);

        // Khởi tạo UI
        edtAmount = findViewById(R.id.edtAmount);
        edtNote = findViewById(R.id.edtNote);
        edtDueDate = findViewById(R.id.edtDueDate);
        spnCategory = findViewById(R.id.spnCategory);
        spnPaymentMethod = findViewById(R.id.spnPaymentMethod);
        btnCreate = findViewById(R.id.btnCreate);
        btnDueDate = findViewById(R.id.btnDueDate);
        tvCreatedDate = findViewById(R.id.tvCreatedDate);
        spnTransactionType = findViewById(R.id.spnTransactionType);

        // Lấy transactionId từ Intent
        Intent intent = getIntent();
        transactionId = intent.getIntExtra("transaction_id", -1);
        Log.d("EditTransaction", "transaction_id nhận được: " + transactionId);
        loadCategories();
        loadPaymentMethods();
        spnTransactionType = findViewById(R.id.spnTransactionType);

        spnTransactionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                transactionType = position == 0 ? "Chi" : "Thu";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Kiểm tra nếu đang chỉnh sửa thì load dữ liệu
        if (transactionId != -1) {
            loadTransactionData(transactionId);
        }
    }


    private void loadCategories() {
        List<String> categories = dbHelper.getAllCategories();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(adapter);
        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void loadPaymentMethods() {
        List<String> accounts = dbHelper.getAllBudgets();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accounts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPaymentMethod.setAdapter(adapter);
        spnPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPaymentMethod = accounts.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }





    private void saveTransaction() {
        String amountStr = edtAmount.getText().toString().trim();
        if (edtNote.getText().toString().trim().isEmpty()) {
            Log.d("DEBUG", "Chưa có ghi chú");
        }


        // Kiểm tra edtDueDate có null không, nếu có thì set giá trị mặc định
        if (edtDueDate.getText().toString().trim().isEmpty()) {
            edtDueDate.setText("01/01/2025");
        }



        if (tvCreatedDate == null) {
            tvCreatedDate = findViewById(R.id.tvCreatedDate);
        }
        if (tvCreatedDate != null && tvCreatedDate.getText().toString().trim().isEmpty()) {
            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
            tvCreatedDate.setText("Ngày tạo: " + currentDate);
        }



        String note = edtNote.getText().toString().trim();
        String dueDate = edtDueDate.getText().toString().trim();
        if (selectedDate == null) {
            selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
        }

        if (amountStr.isEmpty() || selectedCategory == null || selectedPaymentMethod == null || selectedDate == null || dueDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        int categoryId = dbHelper.getCategoryIdByName(selectedCategory);
        int accountId = dbHelper.getAccountIdByName(selectedPaymentMethod);

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        if (userId != -1) {
            boolean isSuccess;
            if (transactionId == -1) {
                // THÊM MỚI
                isSuccess = dbHelper.insertTransaction(userId, accountId, categoryId, amount, transactionType, selectedDate, dueDate, note); // ✅ Thêm dueDate
            } else {
                // CẬP NHẬT
                isSuccess = dbHelper.updateTransaction(transactionId, accountId, categoryId, amount, transactionType, selectedDate, dueDate, note); // ✅ Thêm dueDate
            }

            if (isSuccess) {

                Toast.makeText(this,"Giao dịch được lưu thành công!",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Transaction.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi lưu giao dịch!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy userId!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTransactionData(int transactionId) {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(this);
        }

        // Lấy thông tin từ database
        Map<String, String> transaction = dbHelper.getTransactionById(transactionId);
        if (transaction == null || transaction.isEmpty()) {
            Log.e("LoadTransaction", "Transaction not found for ID: " + transactionId);
            Toast.makeText(this, "Không tìm thấy giao dịch!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("LoadTransaction", "Transaction Data: " + transaction.toString());
        if (edtAmount != null) {
            edtAmount.setText(transaction.get("amount"));
        } else {
            Log.e("LoadTransaction", "edtAmount is NULL!");
        }

        if (edtNote != null) {
            edtNote.setText(transaction.get("note"));
        } else {
            Log.e("LoadTransaction", "edtNote is NULL!");
        }

        if (edtDueDate != null) {
            edtDueDate.setText(transaction.get("due_date") != null ? transaction.get("due_date") : "Chưa có ngày đáo hạn");
        } else {
            Log.e("LoadTransaction", "edtDueDate is NULL!");
        }



        // Set dữ liệu cho các trường khác
        edtAmount.setText(transaction.get("amount"));
        edtNote.setText(transaction.get("note"));
        selectedDate = transaction.get("date");

        selectedCategory = transaction.get("category_name");
        selectedPaymentMethod = transaction.get("payment_method");

        // Chọn đúng category & phương thức thanh toán
        ArrayAdapter<String> categoryAdapter = (ArrayAdapter<String>) spnCategory.getAdapter();
        if (categoryAdapter != null) {
            int categoryPosition = categoryAdapter.getPosition(selectedCategory);
            if (categoryPosition >= 0) spnCategory.setSelection(categoryPosition);
        }

        ArrayAdapter<String> paymentAdapter = (ArrayAdapter<String>) spnPaymentMethod.getAdapter();
        if (paymentAdapter != null) {
            int paymentPosition = paymentAdapter.getPosition(selectedPaymentMethod);
            if (paymentPosition >= 0) spnPaymentMethod.setSelection(paymentPosition);
        }

        // Chọn loại giao dịch (0: Chi, 1: Thu)
        transactionType = transaction.get("transaction_type");
        if (transactionType != null) {
            spnTransactionType.setSelection(transactionType.equalsIgnoreCase("Chi") ? 0 : 1);
        }
    }







}
