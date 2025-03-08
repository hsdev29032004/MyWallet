package com.example.mywallet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywallet.Activities.BudgetActivity;
import com.example.mywallet.Activities.UpdateBudgetActivity;
import com.example.mywallet.Models.Budget;
import com.example.mywallet.R;

import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    private Context context;
    private List<Budget> budgetList;

    public BudgetAdapter(Context context, List<Budget> budgetList) {
        this.context = context;
        this.budgetList = budgetList;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.budget_item, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) { //onBindViewHolder là một phương thức trong RecyclerView.Adapter có nhiệm vụ gán dữ liệu từ danh sách (List<Budget> budgetList) vào giao diện của từng item trong RecyclerView.
        Budget budget = budgetList.get(position);
        holder.budgetName.setText(budget.getName());

        double remainingAmount = budget.getAmountLimit() - budget.getAmountSpent();
        holder.budgetAmount.setText("Còn lại: " + remainingAmount + "đ");

        int progress = (int) ((budget.getAmountSpent() / budget.getAmountLimit()) * 100);
        holder.budgetProgress.setProgress(progress);

        // Xử lý sự kiện nhấn nút Sửa
        holder.btnEditBudget.setOnClickListener(v -> {
            if (context instanceof BudgetActivity) {
                ((BudgetActivity) context).editBudget(budget.getId()); // Gọi phương thức sửa
            }
        });

        // Xử lý sự kiện nhấn nút Xóa
        holder.btnDeleteBudget.setOnClickListener(v -> {
            if (context instanceof BudgetActivity) {
                ((BudgetActivity) context).deleteBudget(budget.getId(), position); // Gọi phương thức xóa
            }
        });
    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }

    public static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView budgetName, budgetAmount;
        ProgressBar budgetProgress;
        ImageButton btnEditBudget, btnDeleteBudget;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view
            budgetName = itemView.findViewById(R.id.txtBudgetName);
            budgetAmount = itemView.findViewById(R.id.txtBudgetAmount);
            budgetProgress = itemView.findViewById(R.id.progressBudget);
            btnEditBudget = itemView.findViewById(R.id.btnEditBudget);
            btnDeleteBudget = itemView.findViewById(R.id.btnDeleteBudget);
        }
    }
}
