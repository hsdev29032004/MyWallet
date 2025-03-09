package com.example.mywallet.Activities.TransactionPackage;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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


import androidx.appcompat.app.AppCompatActivity;


import com.example.mywallet.Activities.MainActivity;
import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.R;


import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        userId = 1;
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
                if (categoryType.equals("Đi vay") || categoryType.equals("Khoản cho vay")) {
                    edtDueDate.setVisibility(View.VISIBLE);
                    edtDueDate.setOnClickListener(v -> showDatePicker());


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
        String dueDateStr = edtDueDate.getVisibility() == View.VISIBLE ? edtDueDate.getText().toString() : null;
        // Tìm account_id và category_id tương ứng
        int selectedAccountId = db.getAccountIdByName(selectedAccountName);
        int selectedCategoryId = db.getCategoryIdByName(selectedCategoryName);
        Log.d("Transaction", "Selected Account: " + selectedAccountName + ", Account ID: " + selectedAccountId);
        Log.d("Transaction", "Selected Category: " + selectedCategoryName + ", Category ID: " + selectedCategoryId);

        if (dueDateStr != null && !dueDateStr.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date dueDate = sdf.parse(dueDateStr);
                Calendar calDue = Calendar.getInstance();
                calDue.setTime(dueDate);
                // Đặt giờ, phút, giây về 0 để chỉ xét ngày
                calDue.set(Calendar.HOUR_OF_DAY, 0);
                calDue.set(Calendar.MINUTE, 0);
                calDue.set(Calendar.SECOND, 0);
                calDue.set(Calendar.MILLISECOND, 0);

                Calendar calToday = Calendar.getInstance();
                calToday.set(Calendar.HOUR_OF_DAY, 0);
                calToday.set(Calendar.MINUTE, 0);
                calToday.set(Calendar.SECOND, 0);
                calToday.set(Calendar.MILLISECOND, 0);

                if (calDue.before(calToday)) {
                    Toast.makeText(this, "Ngày đến hạn phải sau ngày hiện tại!", Toast.LENGTH_SHORT).show();
                    return; // Dừng lưu giao dịch
                }
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi định dạng ngày!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Kiểm tra nếu user nhập số tiền hợp lệ
        double amount = 0;
        try {
            amount = Double.parseDouble(edtAmount.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra loại danh mục
        String categoryType = db.getCategoryTypeById(selectedCategoryId);
        if (categoryType.equals("Chi")) { // Chỉ kiểm tra ngân sách nếu là chi tiêu
            double remainingBudget = db.getRemainingBudget(userId, selectedCategoryId);
            if (amount > remainingBudget) {
                Toast.makeText(this, "Số tiền vượt quá ngân sách!", Toast.LENGTH_SHORT).show();
                return; // Không lưu giao dịch
            }
        }

        String note = edtNote.getText().toString().isEmpty() ? "Không có ghi chú" : edtNote.getText().toString();
        String dueDate = edtDueDate.getVisibility() == View.VISIBLE ? edtDueDate.getText().toString() : null;

        // Thêm vào database
        boolean success = db.insertTransaction(userId, selectedAccountId, selectedCategoryId, amount, dueDate, note);
        if (success) {
            // Cập nhật số tiền trong tài khoản
            double currentBalance = db.getAccountBalance(selectedAccountId);
            double newBalance = currentBalance;

            // Kiểm tra loại giao dịch để cộng hay trừ số tiền
            if (categoryType.equals("Chi")) {
                newBalance -= amount; // Giảm số tiền nếu là giao dịch chi
            } else if (categoryType.equals("Thu")) {
                newBalance += amount; // Tăng số tiền nếu là giao dịch thu
            }

            // Cập nhật số tiền mới vào tài khoản
            db.updateAccountBalance(selectedAccountId, newBalance);

            Toast.makeText(this, "Giao dịch đã được thêm!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class); // Hoặc màn hình bạn muốn
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi thêm giao dịch!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePicker() {
        // Lấy ngày hiện tại làm mặc định
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Định dạng ngày thành chuỗi yyyy-MM-dd
            String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
            edtDueDate.setText(selectedDate);
        }, year, month, day);


        datePickerDialog.show();
    }


}
