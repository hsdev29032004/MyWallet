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

import androidx.appcompat.app.AppCompatActivity;

import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.R;

import java.util.ArrayList;
import java.util.List;

public class AddCategoryActivity extends AppCompatActivity {
    private Spinner spnCategory;
    private EditText edtCategory;
    private Button btnLuu;
    private ImageButton btnBackToCategory;
    private DatabaseHelper dbHelper;

    private int categoryId = -1;  // Mặc định là -1 (tức là thêm mới)
    private boolean isEditMode = false; // Xác định chế độ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        spnCategory = findViewById(R.id.spnCategory);
        edtCategory = findViewById(R.id.edtCategory);
        btnLuu = findViewById(R.id.btnLuu);
        btnBackToCategory = findViewById(R.id.btnBackToCategory);
        dbHelper = new DatabaseHelper(this);

        // Kiểm tra xem có dữ liệu từ Intent không (nếu có thì là chế độ chỉnh sửa)
        Intent intent = getIntent();
        if (intent.hasExtra("category_id")) {
            isEditMode = true;  // Đang ở chế độ chỉnh sửa
            categoryId = intent.getIntExtra("category_id", -1);
            String categoryName = intent.getStringExtra("category_name");
            String categoryType = intent.getStringExtra("category_type");

            // Hiển thị thông tin danh mục cũ
            edtCategory.setText(categoryName);
            loadCategoryTypes(categoryType);
            btnLuu.setText("Cập nhật"); // Thay đổi chữ của nút
        } else {
            loadCategoryTypes(null);
        }

        btnLuu.setOnClickListener(v -> {
            if (isEditMode) {
                updateCategory(); // Nếu đang chỉnh sửa thì cập nhật danh mục
            } else {
                addCategory(); // Nếu đang thêm mới thì thêm danh mục
            }
        });

        btnBackToCategory.setOnClickListener(v -> finish());
    }

    private void addCategory() {
        String categoryName = edtCategory.getText().toString().trim();
        String categoryType = spnCategory.getSelectedItem().toString();

        if (categoryName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên danh mục!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.insertCategory(categoryName, categoryType);

        if (success) {
            Toast.makeText(this, "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Thêm danh mục thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCategory() {
        String newName = edtCategory.getText().toString().trim();
        String newType = spnCategory.getSelectedItem().toString();

        if (newName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên danh mục!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.updateCategory(categoryId, newName, newType);
        if (success) {
            Toast.makeText(this, "Cập nhật danh mục thành công!", Toast.LENGTH_SHORT).show();

            // Quay về màn hình danh mục và cập nhật lại ListView
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish(); // Đóng activity chỉnh sửa
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật danh mục!", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadCategoryTypes(String selectedType) {
        List<String> items = new ArrayList<>();
        items.add("Thu");
        items.add("Chi");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(adapter);

        if (selectedType != null) {
            spnCategory.setSelection(items.indexOf(selectedType));
        }
    }
}
