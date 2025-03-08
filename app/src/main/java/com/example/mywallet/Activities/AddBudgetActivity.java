package com.example.mywallet.Activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.mywallet.Models.Category;
import com.example.mywallet.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddBudgetActivity extends AppCompatActivity {

    private Spinner spCategory, spAccount;
    private EditText edBudgetName, edtAmount;
    private TextView tvStartDate, tvEndDate;
    private Button btnAdd;
    private ImageButton btnClose, btnCheck;
    private DatabaseHelper dbHelper;
    private List<Category> categories;
    private List<Account> accounts;
    private ArrayAdapter<Category> categoryAdapter;
    private ArrayAdapter<Account> accountAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_budget);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);
        getView();
        // Lấy dl từ database
        categories = dbHelper.getAllCategories_NonDeleted();
        accounts = dbHelper.getAllAccounts_NonDeleted();

        // Kiểm tra và cập nhật adapter cho Categories
        if (categories != null && !categories.isEmpty()) {
            categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCategory.setAdapter(categoryAdapter);
            categoryAdapter.notifyDataSetChanged(); // Cập nhật adapter nếu có dữ liệu
        } else {
            Toast.makeText(this, "Không có danh mục nào", Toast.LENGTH_SHORT).show();
        }

        // Kiểm tra và cập nhật adapter cho Accounts
        if (accounts != null && !accounts.isEmpty()) {
            accountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accounts);
            accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spAccount.setAdapter(accountAdapter);
            accountAdapter.notifyDataSetChanged(); // Cập nhật adapter nếu có dữ liệu
        } else {
            Toast.makeText(this, "Không có tài khoản nào", Toast.LENGTH_SHORT).show();
        }

        // Xử lý sự kiện khi nhấn nút đóng (X)
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng màn hình hiện tại
            }
        });

        // Xử lý sự kiện khi nhấn nút check (✓)
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBudget(); // Gọi hàm thêm ngân sách
            }
        });

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle category selection
                Category selectedCategory = categories.get(position);
                // Do something with the selected category
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle account selection
                Account selectedAccount = accounts.get(position);
                // Do something with the selected account
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnAdd.setOnClickListener(v -> addBudget());
    }
    public void getView(){
        btnClose = findViewById(R.id.btnClose);
        btnCheck = findViewById(R.id.btnSave);
        edtAmount = findViewById(R.id.edtAmount);
        edBudgetName = findViewById(R.id.edBudgetName);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        spAccount = findViewById(R.id.spAccount);
        spCategory = findViewById(R.id.spCategory);
        btnAdd = findViewById(R.id.btnAdd);

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
    public void addBudget() {
        try {
            // Kiểm tra các trường đã được nhập đầy đủ
            String amountText = edtAmount.getText().toString();
            if (amountText.isEmpty()) {
                Toast.makeText(this, "Số tiền không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }
            double amountLimit = Double.parseDouble(amountText);
            if (amountLimit <= 0) {
                Toast.makeText(this, "Số tiền phải lớn hơn 0!", Toast.LENGTH_SHORT).show();
                return;
            }
            String budgetName = edBudgetName.getText().toString();
            if (budgetName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên ngân sách!", Toast.LENGTH_SHORT).show();
                return;
            }
            String startDate = tvStartDate.getText().toString();
            if (startDate.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ngày bắt đầu!", Toast.LENGTH_SHORT).show();
                return;
            }
            String endDate = tvEndDate.getText().toString();
            if (endDate.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ngày kết thúc!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra ngày kết thúc phải lớn hơn ngày bắt đầu
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);
            if (end.before(start)) {
                Toast.makeText(this, "Ngày kết thúc phải lớn hơn ngày bắt đầu!", Toast.LENGTH_SHORT).show();
                return;
            }

            Account selectedAccount = (Account) spAccount.getSelectedItem();
            Category selectedCategory = (Category) spCategory.getSelectedItem();
            int userId = 1;  // Giả định User ID là 1, có thể lấy từ SharedPreferences hoặc Session
            // Kiểm tra số dư tài khoản
            if (selectedAccount.getBalance() < amountLimit) {
                Toast.makeText(this, "Số dư tài khoản không đủ để trừ!", Toast.LENGTH_SHORT).show();
                return;
            }
            long rowId = dbHelper.insertBudget(userId, selectedCategory.getId(), selectedAccount.getId(), budgetName, amountLimit, startDate, endDate);

            if (rowId != -1) {
                dbHelper.deductBalance(selectedAccount.getId(), amountLimit);
                Toast.makeText(this, "Ngân sách đã được lưu thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi lưu ngân sách.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}