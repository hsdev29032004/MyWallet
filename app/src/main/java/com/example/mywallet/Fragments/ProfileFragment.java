package com.example.mywallet.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mywallet.Activities.Budget.BudgetActivity;
import com.example.mywallet.Activities.IntroduceActivity;
import com.example.mywallet.R;

public class ProfileFragment extends Fragment {

    TextView txtTongTien;
    Button btnGioiThieu, btnViewBudget;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnGioiThieu = view.findViewById(R.id.btnGioiThieu);
        btnViewBudget = view.findViewById(R.id.btnViewBudget);



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

        return view;
    }
}
