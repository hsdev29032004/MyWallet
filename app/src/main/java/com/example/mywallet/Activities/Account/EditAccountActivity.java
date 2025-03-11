package com.example.mywallet.Activities.Account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.Models.Account;
import com.example.mywallet.R;

public class EditAccountActivity extends AppCompatActivity {
    private EditText edtAccountName, edtBalance;
    private Button btnSaveAccount;
    private ImageButton btnClose;
    private DatabaseHelper dbHelper;
    private int accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        edtAccountName = findViewById(R.id.edtAccountName);
        edtBalance = findViewById(R.id.edtBalance);
        btnSaveAccount = findViewById(R.id.btnSaveAccount);
        btnClose = findViewById(R.id.btnClose);
        dbHelper = new DatabaseHelper(this);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        accountId = intent.getIntExtra("ACCOUNT_ID", -1);
        edtAccountName.setText(intent.getStringExtra("ACCOUNT_NAME"));
        String balanceStr = intent.getStringExtra("ACCOUNT_BALANCE");
        if (balanceStr != null && !balanceStr.isEmpty()) {
            try {
                double balance = Double.parseDouble(balanceStr);
                edtBalance.setText(String.format("%.0f", balance));
            } catch (NumberFormatException e) {
                edtBalance.setText("0");
            }
        }

        btnSaveAccount.setOnClickListener(v -> saveAccountChanges());
        btnClose.setOnClickListener(v -> finish());
    }

    private void saveAccountChanges() {
        String newName = edtAccountName.getText().toString().trim();
        String balanceText = edtBalance.getText().toString().trim();

        if (newName.isEmpty() || balanceText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double newBalance = Double.parseDouble(balanceText);
            newBalance = Math.floor(newBalance);

            Account updatedAccount = new Account(accountId, newName, newBalance);
            boolean updated = dbHelper.updateAccount(updatedAccount);

            if (updated) {
                Toast.makeText(this, "Cập nhật tài khoản thành công!", Toast.LENGTH_SHORT).show();

                // Gửi kết quả về Fragment
                Intent resultIntent = new Intent();
                resultIntent.putExtra("ACCOUNT_UPDATED", true);
                setResult(RESULT_OK, resultIntent);

                finish(); // Đóng activity và quay lại Fragment
            } else {
                Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số dư không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }
}