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

public class CategoryActivity extends AppCompatActivity {

    GridView gvCategory;
    TabHost tabCategory;
    CategoryAdapter adapterCategory;
    ArrayList<Category> listCategory;

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
    gvCategory = findViewById(R.id.gvCategory);
    tabCategory = findViewById(R.id.tabCategory);

    //Thêm dữ liệu vào list
    listCategory = new ArrayList<>();
    for(int i=0; i<9;i++){
        listCategory.add(new Category(iconCategory, nameCategory + " "+i));
    }

    //Sử dụng adapter
    adapterCategory= new CategoryAdapter(CategoryActivity.this, R.layout.layout_category_item, listCategory);
    gvCategory.setAdapter(adapterCategory);

    //Xử lý tab host
        tabCategory = findViewById(R.id.tabCategory);
        tabCategory.setup();

        //Khai báo tab con
        TabHost.TabSpec spec1, spec2;

        //Ứng với mỗi tab con
        //Tab 1
        spec1 = tabCategory.newTabSpec("tab1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Thu");
        tabCategory.addTab(spec1);

        //Tab 1
        spec2 = tabCategory.newTabSpec("tab2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Chi");
        tabCategory.addTab(spec2);


    }
}