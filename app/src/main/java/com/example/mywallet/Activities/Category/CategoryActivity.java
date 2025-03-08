package com.example.mywallet.Activities.Category;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TabHost;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mywallet.Activities.AddCategoryActivity;
import com.example.mywallet.Adapters.CategoryAdapter;
import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.Models.Category;
import com.example.mywallet.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private TabHost tabCategory;
    private DatabaseHelper dbHelper;
    private GridView gvCategoryThu, gvCategoryChi;
    private CategoryAdapter adapterThu, adapterChi;
    private ImageButton btnBackToTransaction, btnAddCategory;

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
        gvCategoryThu = findViewById(R.id.gvCategoryThu);
        gvCategoryChi = findViewById(R.id.gvCategoryChi);
        tabCategory = findViewById(R.id.tabCategory);
        btnBackToTransaction = findViewById(R.id.btnBackToTransaction);
        btnAddCategory = findViewById(R.id.btnAddCategory);

        //Lấy dữ liệu ra khỏi csdl
        dbHelper = new DatabaseHelper(this);


        //Hiển thị dữ liệu và
        loadCategories();
        setupTabHost();

        //Xử lý sự kiện click vào các danh mục
        setupGridViewClickListeners();

        //Xử lý sự kiện click nút quay lại ở đây
        btnBackToTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();//Đóng
            }
        });

        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryActivity.this, AddCategoryActivity.class);
                startActivity(intent);
            }
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


        //Thêm dữ liệu vào list
        int defaultIcon = R.drawable.account; //icon mặc định
        while (listCategoryThu.size() < 9) {
            listCategoryThu.add(new Category(0, defaultIcon, "Chưa chọn", "Thu"));
        }
        while (listCategoryChi.size() < 9) {
            listCategoryChi.add(new Category(0, defaultIcon, "Chưa chọn", "Chi"));
        }


        //Sử dụng adapter
        adapterThu = new CategoryAdapter(CategoryActivity.this, R.layout.layout_category_item, new ArrayList<>(listCategoryThu));
        adapterChi = new CategoryAdapter(CategoryActivity.this, R.layout.layout_category_item, new ArrayList<>(listCategoryChi));

        gvCategoryThu.setAdapter(adapterThu);
        gvCategoryChi.setAdapter(adapterChi);
    }

    // Hàm xử lý sự kiện khi người dùng chọn danh mục từ GridView
    private void setupGridViewClickListeners() {
        gvCategoryThu.setOnItemClickListener((parent, view, position, id) -> handleCategorySelection(adapterThu, position));
        gvCategoryChi.setOnItemClickListener((parent, view, position, id) -> handleCategorySelection(adapterChi, position));
    }

    // Hàm xử lý logic khi chọn một danh mục
    private void handleCategorySelection(CategoryAdapter adapter, int position) {
        Category selectedCategory = adapter.getItem(position);
        if (selectedCategory != null && !"Chưa chọn".equals(selectedCategory.getName())) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("category_id", selectedCategory.getId());
            resultIntent.putExtra("category_name", selectedCategory.getName());
            resultIntent.putExtra("category_type", selectedCategory.getType());

            setResult(RESULT_OK, resultIntent);
            // finish(); // Nếu muốn đóng Activity sau khi chọn danh mục
        }
    }

}