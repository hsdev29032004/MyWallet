package com.example.mywallet.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mywallet.Fragments.History.ExpenseFragment;
import com.example.mywallet.Fragments.History.IncomeFragment;

public class HistoryAdapter extends FragmentStateAdapter {

    public HistoryAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new IncomeFragment();
            case 1:
                return new ExpenseFragment();
            default:
                return new IncomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
