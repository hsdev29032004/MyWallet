package com.example.mywallet.Activities.Category;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mywallet.Adapters.CategoryAdapter;
import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.Models.Category;
import com.example.mywallet.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private TabHost tabCategory;
    private DatabaseHelper dbHelper;
    private ListView lvCategoryThu, lvCategoryChi;
    private CategoryAdapter adapterThu, adapterChi;
    private ImageButton btnBackToTransaction, btnAddCategory;
    private boolean isFromTransaction = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //ánh xạ id
        lvCategoryThu = findViewById(R.id.lvCategoryThu);
        lvCategoryChi = findViewById(R.id.lvCategoryChi);
        tabCategory = findViewById(R.id.tabCategory);
        btnBackToTransaction = findViewById(R.id.btnBackToTransaction);
        btnAddCategory = findViewById(R.id.btnAddCategory);

        //Lấy dữ liệu ra khỏi csdl
        dbHelper = new DatabaseHelper(this);

        Intent myIntent = getIntent();
        if (myIntent.hasExtra("isFromTransaction")) {
            isFromTransaction = myIntent.getBooleanExtra("isFromTransaction", false);
        }

        //Hiển thị dữ liệu và
        loadCategories();
        setupTabHost();

        //Xử lý sự kiện click vào các danh mục
        setupListViewClickListeners();

        //Xử lý sự kiện click nút quay lại ở đây
        btnBackToTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//Đóng activity này trở về activity trước đó
            }
        });

        btnAddCategory.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryActivity.this, AddCategoryActivity.class);
            startActivityForResult(intent, 1); // Sử dụng startActivityForResult để nhận kết quả
        });


    }

    private void setupTabHost() {
        tabCategory.setup();

        TabHost.TabSpec specChi = tabCategory.newTabSpec("tabChi");
        specChi.setContent(R.id.tab1);
        specChi.setIndicator("Chi");
        tabCategory.addTab(specChi);

        TabHost.TabSpec specThu = tabCategory.newTabSpec("tabThu");
        specThu.setContent(R.id.tab2);
        specThu.setIndicator("Thu");
        tabCategory.addTab(specThu);
    }

    private void loadCategories() {
        List<Category> listCategoryThu = dbHelper.getCategoriesByType("Thu");
        List<Category> listCategoryChi = dbHelper.getCategoriesByType("Chi");


        //Sử dụng adapter
        adapterThu = new CategoryAdapter(CategoryActivity.this, R.layout.layout_category_item, new ArrayList<>(listCategoryThu));
        adapterChi = new CategoryAdapter(CategoryActivity.this, R.layout.layout_category_item, new ArrayList<>(listCategoryChi));

        lvCategoryThu.setAdapter(adapterThu);
        lvCategoryChi.setAdapter(adapterChi);
    }

    // Hàm xử lý sự kiện khi người dùng chọn danh mục từ ListView
    private void setupListViewClickListeners() {
        lvCategoryThu.setOnItemClickListener((parent, view, position, id) -> handleCategorySelection(adapterThu, position));
        lvCategoryChi.setOnItemClickListener((parent, view, position, id) -> handleCategorySelection(adapterChi, position));
    }


    // Hàm xử lý logic khi chọn một danh mục
    private void handleCategorySelection(CategoryAdapter adapter, int position) {
        Category selectedCategory = adapter.getItem(position);
        if (selectedCategory != null ) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("category_id", selectedCategory.getId());
            resultIntent.putExtra("category_name", selectedCategory.getName());
            resultIntent.putExtra("category_type", selectedCategory.getType());

            setResult(RESULT_OK, resultIntent);
            // Nếu mở từ TransactionActivity thì đóng Activity
            if (isFromTransaction) {
                finish();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            boolean isUpdated = data.getBooleanExtra("is_updated", false);
            if (isUpdated) {
                loadCategories(); // Cập nhật lại danh sách danh mục
            }
        }
    }



}