package com.example.mywallet.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywallet.Activities.AddAccountActivity;
import com.example.mywallet.Adapters.AccountAdapter;
import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.Models.Account;
import com.example.mywallet.R;

import java.util.List;

public class AccountFragment extends Fragment {
    private RecyclerView recyclerView;
    private AccountAdapter accountAdapter;
    private DatabaseHelper dbHelper;
    private TextView txtNoAccount;
    private View rootView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_account, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerViewAccounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DatabaseHelper(requireContext());
        txtNoAccount = rootView.findViewById(R.id.txtNoAccount);

        loadAccounts();

        ImageButton btnAddAccount = rootView.findViewById(R.id.btnAddAccount);
        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddAccountActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void loadAccounts() {
        List<Account> accountList = dbHelper.getAccountsByUserId(1);

        if (accountAdapter == null) {
            accountAdapter = new AccountAdapter(getContext(), accountList);
            recyclerView.setAdapter(accountAdapter);
        } else {
            accountAdapter.updateData(accountList);
        }

        if (accountList == null || accountList.isEmpty()) {
            txtNoAccount.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Không có tài khoản nào", Toast.LENGTH_SHORT).show();
        } else {
            txtNoAccount.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAccounts();
    }
}
