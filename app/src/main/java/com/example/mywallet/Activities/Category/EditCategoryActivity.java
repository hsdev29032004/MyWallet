package com.example.mywallet.Activities.Category;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.R;

public class EditCategoryActivity extends AppCompatActivity {

    private ImageButton btnBackToCategory;
    private EditText edtCategory;
    private Button btnUpdateCategory;
    private DatabaseHelper dbHelper;
    private int categoryId = -1;  // Mặc định là -1 (tức là thêm mới)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Ánh xạ
        btnBackToCategory = findViewById(R.id.btnBackToCategory);
        edtCategory = findViewById(R.id.edtCategory);
        btnUpdateCategory = findViewById(R.id.btnUpdateCategory);
        dbHelper = new DatabaseHelper(this);

        // Lấy dữ liệu từ intent
        Intent intent = getIntent();
        categoryId = intent.getIntExtra("category_id", -1);
        String categoryName = intent.getStringExtra("category_name");
        String categoryType = intent.getStringExtra("category_type");


        // Hiển thị thông tin danh mục cũ
        edtCategory.setText(categoryName);

        btnUpdateCategory.setOnClickListener(v -> updateCategory());
        btnBackToCategory.setOnClickListener(v -> finish());
    }

    private void updateCategory() {
        String newName = edtCategory.getText().toString().trim();

        if (newName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên danh mục!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đọc lại loại category từ database
        String categoryType = dbHelper.getCategoryTypeById(categoryId);

        boolean success = dbHelper.updateCategory(categoryId, newName, categoryType);
        if (success) {
            Toast.makeText(this, "Cập nhật danh mục thành công!", Toast.LENGTH_SHORT).show();

            // Gửi dữ liệu cập nhật về CategoryActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("is_updated", true);
            setResult(RESULT_OK, resultIntent);

            finish();
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật danh mục!", Toast.LENGTH_SHORT).show();
        }
    }



}