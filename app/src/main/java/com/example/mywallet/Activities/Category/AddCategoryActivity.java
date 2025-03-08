package com.example.mywallet.Activities.Category;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.R;

import java.util.ArrayList;
import java.util.List;

public class AddCategoryActivity extends AppCompatActivity {
    private Spinner spnCategory;
    private List<String> items = new ArrayList<>();
    private EditText edtCategory;
    private Button btnLuu;
    private ImageButton btnBackToCategory;
    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Ánh xạ id
        spnCategory = findViewById(R.id.spnCategory);
        edtCategory = findViewById(R.id.edtCategory);
        btnLuu = findViewById(R.id.btnLuu);
        btnBackToCategory = findViewById(R.id.btnBackToCategory);
        dbHelper = new DatabaseHelper(this);



        loadCategoryTypes(); //Thêm dữ liệu vào spinner

        //Xử lý sự kiện nút lưu
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });

        //Xử lý sự kiện nút quay lại
        btnBackToCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

    }

    private void addCategory() {
        String categoryName = edtCategory.getText().toString().trim();
        String categoryType = spnCategory.getSelectedItem().toString();

        // Kiểm tra dữ liệu hợp lệ
        if (categoryName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên danh mục!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thêm danh mục vào database
        boolean success = dbHelper.insertCategory(categoryName, categoryType);

        if (success) {
            Toast.makeText(this, "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();

            // Chuyển sang CategoryActivity
            Intent intent = new Intent(AddCategoryActivity.this, CategoryActivity.class);
            startActivity(intent);

            // Kết thúc Activity hiện tại để không quay lại khi nhấn nút back
            finish();
        } else {
            Toast.makeText(this, "Thêm danh mục thất bại!", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadCategoryTypes() {
        List<String> items = new ArrayList<>();
        items.add("Thu");
        items.add("Chi");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(adapter);
    }
}