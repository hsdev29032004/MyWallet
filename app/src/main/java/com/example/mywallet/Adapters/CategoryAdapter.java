package com.example.mywallet.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mywallet.Activities.Category.AddCategoryActivity;
import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.Models.Category;
import com.example.mywallet.R;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> {
    Activity context;
    int idLayout;
    ArrayList<Category> listCategory;

    public CategoryAdapter(Activity context, int idLayout, ArrayList<Category> listCategory) {
        super(context, idLayout, listCategory);
        this.context = context;
        this.idLayout = idLayout;
        this.listCategory = listCategory;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(idLayout, null);

        Category category = listCategory.get(position);

        TextView txtType = convertView.findViewById(R.id.txtCategoryType);
        txtType.setText(category.getType());

        TextView txtName = convertView.findViewById(R.id.txtCategoryName);
        txtName.setText(category.getName());

        ImageView btnDelete = convertView.findViewById(R.id.btnDeleteCategory);
        ImageView btnEdit = convertView.findViewById(R.id.btnEditCategory);

        DatabaseHelper dbHelper = new DatabaseHelper(context);

        // Sự kiện xoá danh mục
        btnDelete.setOnClickListener(v -> {
            boolean isDeleted = dbHelper.deleteCategory(category.getId());
            if (isDeleted) {
                listCategory.remove(position);
                notifyDataSetChanged(); // Cập nhật lại danh sách
                Toast.makeText(context, "Đã xoá danh mục!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Lỗi khi xoá danh mục!", Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện chỉnh sửa danh mục
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddCategoryActivity.class);
            intent.putExtra("category_id", category.getId());
            intent.putExtra("category_name", category.getName());
            intent.putExtra("category_type", category.getType());
            //context.startActivity(intent);
            ((Activity) context).startActivityForResult(intent, 1);
        });


        return convertView;
    }

}
