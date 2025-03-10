package com.example.mywallet.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywallet.Activities.Budget.BudgetActivity;
import com.example.mywallet.Activities.Budget.UpdateBudgetActivity;
import com.example.mywallet.Activities.Budget.BudgetActivity;
import com.example.mywallet.Activities.MainActivity;
import com.example.mywallet.Activities.Budget.UpdateBudgetActivity;
import com.example.mywallet.Adapters.BudgetAdapter;
import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.Fragments.History.HistoryFragment;
import com.example.mywallet.Interface.BudgetActionListener;
import com.example.mywallet.Models.Budget;
import com.example.mywallet.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements BudgetActionListener {
    private TextView tvTotalBalance,tvIncome, tvTotal, tvExpense, tvXemThem, tvHistory;
    private PieChart pieChart;
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private BudgetAdapter budgetAdapter;
    private List<Budget> budgetList;
    private ImageView btnEye;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Ánh xạ UI
        tvTotalBalance = view.findViewById(R.id.tvTotalBalance);
        btnEye = view.findViewById(R.id.btnEye);
        tvXemThem = view.findViewById(R.id.tvXemThem);
        tvIncome = view.findViewById(R.id.tvIncome);
        tvExpense = view.findViewById(R.id.tvExpense);
        tvTotal = view.findViewById(R.id.tvTotal);
        pieChart = view.findViewById(R.id.pieChart);
        tvHistory = view.findViewById(R.id.tvHistory);
        dbHelper = new DatabaseHelper(getContext());

        // Khởi tạo RecyclerView và Adapter
        recyclerView = view.findViewById(R.id.recyclerView);
        dbHelper = new DatabaseHelper(getContext());
        // Giả sử userId là 1 (Bạn có thể lấy từ SharedPreferences nếu có)
        int userId = 1;
        budgetList = dbHelper.getAllBudgets(userId);
        budgetAdapter = new BudgetAdapter(budgetList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(budgetAdapter);

        // Load dữ liệu từ DB
        loadTotalBalance();
        loadPieChart();

        // Ẩn hiện số dư
        final boolean[] isBalanceVisible = {true}; // Trạng thái hiển thị
        final String[] originalBalance = {tvTotalBalance.getText().toString()}; // Lưu giá trị ban đầu
        btnEye.setOnClickListener(v -> {
            if (isBalanceVisible[0]) {
                tvTotalBalance.setText("******");
                btnEye.setImageResource(R.drawable.baseline_visibility_off_24);
            } else {
                tvTotalBalance.setText(originalBalance[0]); // Hthị lại số dư
                btnEye.setImageResource(R.drawable.baseline_remove_red_eye_24);
            }
            isBalanceVisible[0] = !isBalanceVisible[0]; // Đảo trạng thái
        });


        tvXemThem.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BudgetActivity.class);
            startActivity(intent);
        });
        tvHistory.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, new HistoryFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        //Sự kiện click dấu +
//        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);
//        fabAdd.setOnClickListener(v -> {
//            // Intent intent = new Intent(getActivity(), TransactionActivity.class);
//            // startActivity(intent);
//        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshBudgetList();
    }

    private void refreshBudgetList() {
        int userId = 1; // Giả sử userId là 1
        budgetList.clear();
        budgetList.addAll(dbHelper.getAllBudgets_limit(userId));
        budgetAdapter.notifyDataSetChanged();
    }

    @Override
    public void editBudget(int budgetId) {
        Intent intent = new Intent(getActivity(), UpdateBudgetActivity.class);
        intent.putExtra("BUDGET_ID", budgetId);
        startActivity(intent);
    }

    @Override
    public void deleteBudget(int budgetId, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa ngân sách này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (dbHelper.deleteBudget(budgetId)) {
                        budgetList.remove(position);
                        budgetAdapter.notifyItemRemoved(position);
                        Toast.makeText(getContext(), "Đã xóa ngân sách", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void loadTotalBalance() {
        double totalBalance = dbHelper.getTotalBalance();
        double totalIncome = 0, totalExpense = 0;

        // Lấy cơ sở dữ liệu ở chế độ đọc
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Lấy tổng thu (các giao dịch thuộc danh mục có type = 'Thu')
        Cursor cursor = db.rawQuery(
                "SELECT SUM(t." + DatabaseHelper.COLUMN_AMOUNT + ") " +
                        "FROM " + DatabaseHelper.TABLE_TRANSACTION + " t " +
                        "JOIN " + DatabaseHelper.TABLE_CATEGORY + " c " +
                        "ON t." + DatabaseHelper.COLUMN_CATEGORY_ID + " = c." + DatabaseHelper.COLUMN_CATEGORY_ID + " " +
                        "WHERE c." + DatabaseHelper.COLUMN_CATEGORY_TYPE + " = ?",
                new String[]{"Thu"}
        );
        if (cursor.moveToFirst() && cursor.getString(0) != null) {
            totalIncome = cursor.getDouble(0);
        }
        cursor.close();

        // Lấy tổng chi (các giao dịch thuộc danh mục có type = 'Chi')
        cursor = db.rawQuery(
                "SELECT SUM(t." + DatabaseHelper.COLUMN_AMOUNT + ") " +
                        "FROM " + DatabaseHelper.TABLE_TRANSACTION + " t " +
                        "JOIN " + DatabaseHelper.TABLE_CATEGORY + " c " +
                        "ON t." + DatabaseHelper.COLUMN_CATEGORY_ID + " = c." + DatabaseHelper.COLUMN_CATEGORY_ID + " " +
                        "WHERE c." + DatabaseHelper.COLUMN_CATEGORY_TYPE + " = ?",
                new String[]{"Chi"}
        );
        if (cursor.moveToFirst() && cursor.getString(0) != null) {
            totalExpense = cursor.getDouble(0);
        }
        cursor.close();
        db.close();

        // Tính tổng số dư
        double total = totalIncome - totalExpense;

        // Định dạng số tiền có dấu chấm theo chuẩn Việt Nam
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedTotalBalance = formatter.format(totalBalance) + " đ";
        String formattedIncome = formatter.format(totalIncome) + " đ";
        String formattedExpense = formatter.format(totalExpense) + " đ";
        String formattedTotal = formatter.format(total) + " đ";

        // Cập nhật UI
        tvTotalBalance.setText(formattedTotalBalance);
        tvIncome.setText(formattedIncome);
        tvExpense.setText(formattedExpense);
        tvTotal.setText(formattedTotal);
    }


    private void loadPieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        float totalIncome = 0, totalExpense = 0;

        // Truy vấn tổng thu nhập từ bảng Category
        Cursor cursor = db.rawQuery(
                "SELECT SUM(t." + DatabaseHelper.COLUMN_AMOUNT + ") " +
                        "FROM " + DatabaseHelper.TABLE_TRANSACTION + " t " +
                        "JOIN " + DatabaseHelper.TABLE_CATEGORY + " c " +
                        "ON t." + DatabaseHelper.COLUMN_CATEGORY_ID + " = c." + DatabaseHelper.COLUMN_CATEGORY_ID + " " +
                        "WHERE c." + DatabaseHelper.COLUMN_CATEGORY_TYPE + " = ?",
                new String[]{"Thu"}
        );
        if (cursor.moveToFirst() && cursor.getString(0) != null) {
            totalIncome = cursor.getFloat(0);
        }
        cursor.close();

        // Truy vấn tổng chi tiêu từ bảng Category
        cursor = db.rawQuery(
                "SELECT SUM(t." + DatabaseHelper.COLUMN_AMOUNT + ") " +
                        "FROM " + DatabaseHelper.TABLE_TRANSACTION + " t " +
                        "JOIN " + DatabaseHelper.TABLE_CATEGORY + " c " +
                        "ON t." + DatabaseHelper.COLUMN_CATEGORY_ID + " = c." + DatabaseHelper.COLUMN_CATEGORY_ID + " " +
                        "WHERE c." + DatabaseHelper.COLUMN_CATEGORY_TYPE + " = ?",
                new String[]{"Chi"}
        );
        if (cursor.moveToFirst() && cursor.getString(0) != null) {
            totalExpense = cursor.getFloat(0);
        }
        cursor.close();
        db.close();

        // Thêm dữ liệu vào biểu đồ
        ArrayList<Integer> colors = new ArrayList<>();
        if (totalIncome > 0) {
            entries.add(new PieEntry(totalIncome, "Thu nhập"));
            colors.add(Color.parseColor("#32b849")); // Xanh lá cho Thu nhập
        }
        if (totalExpense > 0) {
            entries.add(new PieEntry(totalExpense, "Chi tiêu"));
            colors.add(Color.parseColor("#ef5361")); // Đỏ đậm cho Chi tiêu
        }

        // Thêm dữ liệu vào PieChart
        PieDataSet dataSet = new PieDataSet(entries, ""); // Không có tiêu đề
        dataSet.setColors(colors);
        dataSet.setValueTextSize(13f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setDrawValues(true); // Hiển thị giá trị (phần trăm)

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.1f%%", value); // Chỉ hiển thị phần trăm
            }
        });

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true); // Hiển thị dưới dạng %
        pieChart.getDescription().setEnabled(false);

        // Đặt tiêu đề cho biểu đồ
        pieChart.setCenterText("Tổng quan\nTài chính");
        pieChart.setCenterTextSize(14f);
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);

        // Tùy chỉnh chú thích (legend)
        Legend legend = pieChart.getLegend();
        legend.setTextSize(14f);
        legend.setTextColor(Color.BLACK);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setXEntrySpace(20f);
        legend.setYEntrySpace(10f);

        pieChart.animateY(1000, Easing.EaseInOutCubic);
    }




}
