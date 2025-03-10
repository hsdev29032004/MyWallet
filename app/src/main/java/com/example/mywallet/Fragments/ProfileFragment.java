package com.example.mywallet.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mywallet.Activities.Budget.BudgetActivity;
import com.example.mywallet.Activities.Category.CategoryActivity;
import com.example.mywallet.Activities.IntroduceActivity;
import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.R;

import java.text.NumberFormat;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    TextView txtTongTien;
    private DatabaseHelper dbHelper;
    Button btnGioiThieu, btnViewBudget, btnViewCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnGioiThieu = view.findViewById(R.id.btnGioiThieu);
        btnViewBudget = view.findViewById(R.id.btnViewBudget);
        btnViewCategory = view.findViewById(R.id.btnViewCategory);
        txtTongTien = view.findViewById(R.id.txtTongTien);
        dbHelper = new DatabaseHelper(getContext());




        btnViewBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BudgetActivity.class);
                startActivity(intent);
            }
        });

        btnGioiThieu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), IntroduceActivity.class);
                startActivity(intent);
            }
        });

        btnViewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                startActivity(intent);
            }
        });

        loadTotalBalance();

        return view;
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
        txtTongTien.setText(formattedTotalBalance);

    }
}
