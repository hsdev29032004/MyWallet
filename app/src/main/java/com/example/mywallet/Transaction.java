package com.example.mywallet;


import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.Calendar;


import java.util.List;
public class Transaction extends AppCompatActivity {


    private DatabaseHelper dbHelper;
    private EditText edtAmount, edtNote;
    private Spinner spnCategory, spnPaymentMethod;
    private Button btnCreate, btnDate;
    private String selectedCategory, selectedPaymentMethod, selectedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);


        dbHelper = new DatabaseHelper(this);
        edtAmount = findViewById(R.id.edtAmount);
        edtNote = findViewById(R.id.edtNote);
        spnCategory = findViewById(R.id.spnCategory);
        spnPaymentMethod = findViewById(R.id.spnPaymentMethod);
        btnCreate = findViewById(R.id.btnCreate);
        btnDate = findViewById(R.id.btnDate);


        // Load dữ liệu cho Spinner
        loadCategories();
        loadPaymentMethods();


        // Chọn ngày
        btnDate.setOnClickListener(v -> showDatePicker());


        // Lưu giao dịch
        btnCreate.setOnClickListener(v -> saveTransaction());
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


    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    btnDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }


    private void saveTransaction() {
        String amountStr = edtAmount.getText().toString().trim();
        String note = edtNote.getText().toString().trim();


        if (amountStr.isEmpty() || selectedCategory == null || selectedPaymentMethod == null || selectedDate == null) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }


        double amount = Double.parseDouble(amountStr);
        int categoryId = dbHelper.getCategoryIdByName(selectedCategory);
        int accountId = dbHelper.getAccountIdByName(selectedPaymentMethod);


        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1); // -1 nếu không có userId


        if (userId != -1) { // Kiểm tra xem userId có hợp lệ không
            boolean isInserted = dbHelper.insertTransaction(userId, accountId, categoryId, amount, "Chi", selectedDate, note);
            if (isInserted) {
                Toast.makeText(this, "Giao dịch được thêm thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi khi thêm giao dịch!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy userId!", Toast.LENGTH_SHORT).show();
        }


    }
}
