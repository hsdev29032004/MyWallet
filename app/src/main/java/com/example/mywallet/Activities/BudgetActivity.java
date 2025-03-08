package com.example.mywallet.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywallet.Adapters.BudgetAdapter;
import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.Models.Budget;
import com.example.mywallet.R;

import java.util.List;

public class BudgetActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BudgetAdapter budgetAdapter;
    private List<Budget> budgetList;
    private DatabaseHelper dbHelper;
    ImageButton btnAddBudget, btnClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_budget);

        btnAddBudget = findViewById(R.id.btnAddBudget);
        btnClose = findViewById(R.id.btnClose);
        btnAddBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BudgetActivity.this, AddBudgetActivity.class);
                startActivity(intent);
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Khởi tạo RecyclerView và Adapter
        recyclerView = findViewById(R.id.recyclerView);
        dbHelper = new DatabaseHelper(this);
        // Giả sử userId là 1
        int userId = 1;
        budgetList = dbHelper.getAllBudgets(userId);        // Lấy tất cả ngân sách của người dùng
        budgetAdapter = new BudgetAdapter(this, budgetList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(budgetAdapter);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        refreshBudgetList();
    }

    private void refreshBudgetList() {
        int userId = 1; // Giả sử userId là 1
        budgetList.clear(); // Xóa danh sách cũ
        budgetList.addAll(dbHelper.getAllBudgets(userId)); // Lấy dữ liệu mới từ database
        budgetAdapter.notifyDataSetChanged(); // Cập nhật lại RecyclerView
    }

    // Phương thức xử lý khi nhấn nút "Sửa"
    public void editBudget(int budgetId) {
        Intent intent = new Intent(BudgetActivity.this, UpdateBudgetActivity.class);
        intent.putExtra("BUDGET_ID", budgetId); // Truyền budgetId qua Intent
        startActivity(intent);
    }

    // Phương thức xử lý khi nhấn nút "Xóa"
    public void deleteBudget(int budgetId, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa ngân sách này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Gọi phương thức xóa ngân sách từ cơ sở dữ liệu
                    if (dbHelper.deleteBudget(budgetId)) {
                        budgetList.remove(position); // Xóa ngân sách khỏi danh sách
                        budgetAdapter.notifyItemRemoved(position); // Cập nhật RecyclerView
                        Toast.makeText(this, "Đã xóa ngân sách", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}