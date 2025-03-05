package com.example.mywallet.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.Models.Account;
import com.example.mywallet.R;

public class AddAccountActivity extends AppCompatActivity {

    private EditText edtAccountName, edtBalance;
    private Button btnSaveAccount;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        edtAccountName = findViewById(R.id.edtAccountName);
        edtBalance = findViewById(R.id.edtBalance);
        btnSaveAccount = findViewById(R.id.btnSaveAccount);
        dbHelper = new DatabaseHelper(this);

        btnSaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccount();
            }
        });

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveAccount() {
        String accountName = edtAccountName.getText().toString().trim();
        String balanceStr = edtBalance.getText().toString().trim();

        if (accountName.isEmpty() || balanceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        double balance = Double.parseDouble(balanceStr);
        Account newAccount = new Account(accountName, balance);
        long result = dbHelper.insertAccount(newAccount);

        if (result != -1) {
            Toast.makeText(this, "Thêm tài khoản thành công!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Thêm tài khoản thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}