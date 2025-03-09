package com.example.mywallet.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywallet.Interface.BudgetActionListener;
import com.example.mywallet.Models.Budget;
import com.example.mywallet.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    private List<Budget> budgetList;
    private BudgetActionListener actionListener;

    public BudgetAdapter(List<Budget> budgetList, BudgetActionListener actionListener) {
        this.budgetList = budgetList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_item, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        Budget budget = budgetList.get(position);
        holder.budgetName.setText(budget.getName());

        // Định dạng số tiền còn lại
        double remainingAmount = budget.getAmountLimit() - budget.getAmountSpent();
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedRemainingAmount = formatter.format(remainingAmount) + " đ";

        holder.budgetAmount.setText("Còn lại: " + formattedRemainingAmount);

        // Tính phần trăm và cập nhật ProgressBar
        int progress = (int) ((budget.getAmountSpent() / budget.getAmountLimit()) * 100);
        holder.budgetProgress.setProgress(progress);

        // Xử lý sự kiện khi nhấn nút
        holder.btnEditBudget.setOnClickListener(v -> actionListener.editBudget(budget.getId()));
        holder.btnDeleteBudget.setOnClickListener(v -> actionListener.deleteBudget(budget.getId(), position));
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
            budgetName = itemView.findViewById(R.id.txtBudgetName);
            budgetAmount = itemView.findViewById(R.id.txtBudgetAmount);
            budgetProgress = itemView.findViewById(R.id.progressBudget);
            btnEditBudget = itemView.findViewById(R.id.btnEditBudget);
            btnDeleteBudget = itemView.findViewById(R.id.btnDeleteBudget);
        }
    }
}
