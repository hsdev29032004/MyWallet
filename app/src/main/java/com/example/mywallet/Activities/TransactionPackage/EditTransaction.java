package com.example.mywallet.Activities.TransactionPackage;

import com.example.mywallet.Models.Transaction;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.R;

public class EditTransaction extends AppCompatActivity {

    private EditText edtAmount, edtDueDate, edtNote;
    private TextView txtDate;
    private Button btnUpdate;
    private Spinner spinnerAccount, spinnerCategory;
    private DatabaseHelper dbHelper;
    private int transactionId;
    private int userId; // Đảm bảo bạn có cách lấy userId
    private List<String> accountList;
    private List<String> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction); // Giao diện XML đã cập nhật

        edtAmount = findViewById(R.id.edtAmount);
        edtDueDate = findViewById(R.id.edtDueDate);
        edtNote = findViewById(R.id.edtNote);
        txtDate = findViewById(R.id.txtDate);
        btnUpdate = findViewById(R.id.btnAddTransaction);
        spinnerAccount = findViewById(R.id.spinnerAccount);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        dbHelper = new DatabaseHelper(this);

        // Nhận transaction_id và user_id từ Intent
        transactionId = getIntent().getIntExtra("transaction_id", -1);
        userId = 1; // Nếu userId không có, bạn cần xử lý lại

        // Lấy dữ liệu giao dịch từ database và hiển thị
        loadTransactionData();

        // Load tài khoản và danh mục vào Spinner
        loadAccounts();
        loadCategories();

        // Chọn ngày
        txtDate.setOnClickListener(v -> showDatePicker());

        // Xử lý cập nhật
        btnUpdate.setOnClickListener(v -> updateTransaction());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    txtDate.setText("Ngày: " + date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void loadTransactionData() {
        Log.d("CheckTransactionID", "transactionId trước khi gọi DB: " + transactionId);
        Transaction transaction = dbHelper.getTransactionById(transactionId);
        if (transaction == null) {
            Log.e("EditTransaction", "Không tìm thấy giao dịch với ID: " + transactionId);
        } else {
            edtAmount.setText(String.valueOf(transaction.getAmount()));
            txtDate.setText("Ngày: " + transaction.getDate());

            // Kiểm tra nếu dueDate không null thì hiển thị, nếu null thì để trống
            if (transaction.getDueDate() != null) {
                edtDueDate.setText(transaction.getDueDate());
                edtDueDate.setVisibility(View.VISIBLE); // Hiển thị nếu có dueDate
            } else {
                edtDueDate.setVisibility(View.GONE); // Ẩn nếu không có dueDate
            }

            // Kiểm tra nếu note không null thì hiển thị, nếu null thì để trống
            if (transaction.getNote() != null) {
                edtNote.setText(transaction.getNote());
            } else {
                edtNote.setText("");
            }

            // Chọn tài khoản trong Spinner (nếu có ID tài khoản trong transaction)
            // Chọn tài khoản trong Spinner (nếu có ID tài khoản trong transaction)
            if (transaction.getAccountId() != -1 && accountList != null && !accountList.isEmpty()) {
                int accountPosition = getAccountPosition(transaction.getAccountId());
                spinnerAccount.setSelection(accountPosition);
            }

            // Chọn danh mục trong Spinner (nếu có ID danh mục trong transaction)
            if (transaction.getCategoryId() != -1 && categoryList != null && !categoryList.isEmpty()) {
                int categoryPosition = getCategoryPosition(transaction.getCategoryId());
                spinnerCategory.setSelection(categoryPosition);
            }
        }
    }

    private int getAccountPosition(int accountId) {
        // Kiểm tra danh sách tài khoản không bị null trước khi duyệt
        if (accountList != null && !accountList.isEmpty()) {
            for (int i = 0; i < accountList.size(); i++) {
                if (accountList.get(i).equals(dbHelper.getAccountNameById(accountId))) {
                    return i;
                }
            }
        }
        return 0; // Nếu không tìm thấy, mặc định chọn vị trí đầu tiên
    }

    private int getCategoryPosition(int categoryId) {
        // Kiểm tra danh sách danh mục không bị null trước khi duyệt
        if (categoryList != null && !categoryList.isEmpty()) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).equals(dbHelper.getCategoryNameById(categoryId))) {
                    return i;
                }
            }
        }
        return 0; // Nếu không tìm thấy, mặc định chọn vị trí đầu tiên
    }

    private void updateTransaction() {
        // Kiểm tra giá trị amount hợp lệ
        String amountText = edtAmount.getText().toString();
        if (amountText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền!", Toast.LENGTH_SHORT).show();
            Log.e("UpdateTransaction", "Amount is empty!");
            return;
        }

        double amount = 0;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ!", Toast.LENGTH_SHORT).show();
            Log.e("UpdateTransaction", "Amount is not a valid number!");
            return;
        }
        Log.d("UpdateTransaction", "Amount: " + amount);

        // Lấy ngày từ TextView
        String date = txtDate.getText().toString().substring(6); // Lấy ngày từ "Ngày: YYYY-MM-DD"
        Log.d("UpdateTransaction", "Date: " + date);

        // Lấy dueDate nếu có
        String dueDate = edtDueDate.getText().toString();
        if (dueDate.isEmpty() && edtDueDate.getVisibility() == View.VISIBLE) {
            // Nếu dueDate bắt buộc, thông báo lỗi
            Toast.makeText(this, "Vui lòng nhập ngày đáo hạn!", Toast.LENGTH_SHORT).show();
            Log.e("UpdateTransaction", "Due date is empty but required!");
            return;
        }
        Log.d("UpdateTransaction", "DueDate: " + dueDate);

        // Lấy note từ EditText
        String note = edtNote.getText().toString();
        Log.d("UpdateTransaction", "Note: " + note);

        // Lấy ID của tài khoản và danh mục từ spinner (tìm tài khoản và danh mục thực tế từ danh sách)
        String accountName = accountList.get(spinnerAccount.getSelectedItemPosition());
        String categoryName = categoryList.get(spinnerCategory.getSelectedItemPosition());
        Log.d("UpdateTransaction", "Account: " + accountName + ", Category: " + categoryName);

        int accountId = dbHelper.getAccountIdByName(accountName);
        int categoryId = dbHelper.getCategoryIdByName(categoryName);
        Log.d("UpdateTransaction", "AccountID: " + accountId + ", CategoryID: " + categoryId);

        // Kiểm tra nếu accountId hoặc categoryId không hợp lệ
        if (accountId == -1 || categoryId == -1) {
            Toast.makeText(this, "Tài khoản hoặc danh mục không hợp lệ!", Toast.LENGTH_SHORT).show();
            Log.e("UpdateTransaction", "Invalid AccountID or CategoryID!");
            return;
        }

        // Cập nhật giao dịch
        boolean success = dbHelper.updateTransaction(transactionId, amount, date, dueDate, note, accountId, categoryId);
        if (success) {
            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            Log.d("UpdateTransaction", "Transaction updated successfully");
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            Log.e("UpdateTransaction", "Failed to update transaction");
        }
    }


    private void loadAccounts() {
        accountList = dbHelper.getAccountsByUser(userId); // Danh sách tên tài khoản
        if (accountList != null && !accountList.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, accountList);
            spinnerAccount.setAdapter(adapter);
        } else {
            Log.e("EditTransaction", "Không có tài khoản nào được tải.");
        }
    }

    private void loadCategories() {
        categoryList = dbHelper.getCategories(); // Danh sách tên danh mục
        if (categoryList != null && !categoryList.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryList);
            spinnerCategory.setAdapter(adapter);
        } else {
            Log.e("EditTransaction", "Không có danh mục nào được tải.");
        }
    }

}
