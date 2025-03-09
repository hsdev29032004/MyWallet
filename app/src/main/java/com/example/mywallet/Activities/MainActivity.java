package com.example.mywallet.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.mywallet.Activities.Budget.BudgetActivity;
import com.example.mywallet.Activities.Category.CategoryActivity;
import com.example.mywallet.Activities.Transaction.Transaction;
import com.example.mywallet.Fragments.AccountFragment;
import com.example.mywallet.Fragments.History.HistoryFragment;
import com.example.mywallet.Fragments.HomeFragment;
import com.example.mywallet.Fragments.ProfileFragment;
import com.example.mywallet.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private int userId;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ImageView imageView;

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = prefs.getInt("user_id", 1);

        imageView=findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Tạo mới giao dịch",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this, Transaction.class);
                startActivity(intent);
                finish();
            }
        });

        // Áp dụng padding cho status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        Intent myintent = new Intent(MainActivity.this, CategoryActivity.class);
//        startActivity(myintent);

        // Mặc định hiển thị HomeFragment khi mở app
        loadFragment(new HomeFragment());

        // Xử lý sự kiện click cho BottomNavigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                loadFragment(new HomeFragment());
            } else if (itemId == R.id.nav_account) {
                loadFragment(new AccountFragment());
            } else if (itemId == R.id.nav_history) {
                loadFragment(new HistoryFragment());
            } else if (itemId == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
            } else {
                return false;
            }
            return true;
        });
    }

    // Hàm load Fragment vào container
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
