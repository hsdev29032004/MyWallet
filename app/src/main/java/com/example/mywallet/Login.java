package com.example.mywallet;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class Login extends AppCompatActivity {


    EditText edtEmail, edtPassword;
    TextView txtForgotPassword;
    DatabaseHelper database;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Kiểm tra cơ sở dữ liệu




        setContentView(R.layout.activity_login);  // Đảm bảo sử dụng layout đúng
        // anh xa view
        edtEmail=findViewById(R.id.edtEmail);
        edtPassword=findViewById(R.id.edtPassword);
        txtForgotPassword=findViewById(R.id.txtForgotPassword);
        btnLogin=findViewById(R.id.btnLogin);
        database = new DatabaseHelper(this);
        database.checkDatabase();






        //xu ly su kien dang nhap
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString();




                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(Login.this,"Vui long nhap thong tin email va mat khau", Toast.LENGTH_SHORT).show();


                }else{
                    boolean isValid=database.checkUser(email,password);
                    if(isValid){
                        int userId = database.getUserIdByEmail(email); // Lấy userId từ email


                        // Lưu userId vào SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("user_id", userId);
                        editor.apply();


                        Toast.makeText(Login.this,"Đăng nhập thành công",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(Login.this, MainActivity.class);
                        startActivity(intent);


                    }else {
                        Toast.makeText(Login.this,"Sai tên đăng nhập hoặc mật khẩu",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }



}
