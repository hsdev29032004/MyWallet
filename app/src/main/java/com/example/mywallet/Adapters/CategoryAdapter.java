package com.example.mywallet.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
        //Tạo đế chứa layout
        LayoutInflater Flater = context.getLayoutInflater();

        //Đặt id layout lên đế tạo thành view
        convertView = Flater.inflate(idLayout, null);

        //Lấy phần tử trong mảng category
        Category c = listCategory.get(position);

        //Khai báo, tham chiếu id và hiển thị icon cho category
        ImageView imgIcon = convertView.findViewById(R.id.imgIconCategory);
        imgIcon.setImageResource(c.getIcon());

        //Khai báo, tham chiếu id và hển thị tên của category
        TextView txtName = convertView.findViewById(R.id.txtNameCategory);
        txtName.setText(c.getName());

        return convertView;
    }
}
