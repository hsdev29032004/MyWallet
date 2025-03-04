package com.example.mywallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywallet.Adapters.AccountAdapter;
import com.example.mywallet.DatabaseHelper;
import com.example.mywallet.Models.Account;

import java.util.List;

public class AccountFragment extends Fragment {
    private RecyclerView recyclerView;
    private AccountAdapter accountAdapter;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewAccounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DatabaseHelper(requireContext());
        List<Account> accountList = dbHelper.getAccountsByUserId(1);
        if (accountList == null || accountList.isEmpty()) {
            Toast.makeText(getContext(), "Không có tài khoản nào", Toast.LENGTH_SHORT).show();
        }

        accountAdapter = new AccountAdapter(accountList);
        recyclerView.setAdapter(accountAdapter);

        return view;
    }
}
