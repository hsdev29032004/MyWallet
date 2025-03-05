package com.example.mywallet;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.TabHost;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mywallet.Adapters.CategoryAdapter;
import com.example.mywallet.Models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    TabHost tabCategory;
    CategoryAdapter adapterCategory;
    ArrayList<Category> listCategory;

    DatabaseHelper dbHelper;

    //Dữ liệu thử
    int iconCategory = R.drawable.account;
    String nameCategory = "Danh mục";
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

        addControl();
    }

    private void addControl() {
        //ánh xạ id
        GridView gvCategoryThu = findViewById(R.id.gvCategoryThu);
        GridView gvCategoryChi = findViewById(R.id.gvCategoryChi);
        tabCategory = findViewById(R.id.tabCategory);

        dbHelper = new DatabaseHelper(this);
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
        CategoryAdapter adapterThu = new CategoryAdapter(CategoryActivity.this, R.layout.layout_category_item, new ArrayList<>(listCategoryThu));
        CategoryAdapter adapterChi = new CategoryAdapter(CategoryActivity.this, R.layout.layout_category_item, new ArrayList<>(listCategoryChi));

        gvCategoryThu.setAdapter(adapterThu);
        gvCategoryChi.setAdapter(adapterChi);

    //Xử lý tab host
        tabCategory = findViewById(R.id.tabCategory);
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
}