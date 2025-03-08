package com.example.mywallet.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.mywallet.Activities.Budget.BudgetActivity;
import com.example.mywallet.R;

public class HomeFragment extends Fragment {

    private TextView tvAddBudget;
    private ImageView ivAddBudget;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvAddBudget = view.findViewById(R.id.tvAddBudget);
        ivAddBudget = view.findViewById(R.id.ivAddBudget);

        View.OnClickListener openBudgetActivity = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BudgetActivity.class);
                startActivity(intent);
            }
        };

        tvAddBudget.setOnClickListener(openBudgetActivity);
        ivAddBudget.setOnClickListener(openBudgetActivity);

        return view;
    }
}