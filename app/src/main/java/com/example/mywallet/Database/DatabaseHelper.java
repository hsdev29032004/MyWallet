package com.example.mywallet.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mywallet.Models.Account;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "finance_manager.db";
    private static final int DATABASE_VERSION = 1;

    // Bảng User
    private static final String TABLE_USER = "User";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_FULL_NAME = "full_name";
    private static final String COLUMN_TOKEN = "token";

    // Bảng Account
    private static final String TABLE_ACCOUNT = "Account";
    private static final String COLUMN_ACCOUNT_ID = "account_id";
    private static final String COLUMN_ACCOUNT_NAME = "name";
    private static final String COLUMN_BALANCE = "balance";
    private static final String COLUMN_IS_DELETED = "isDeleted";

    // Bảng Category
    private static final String TABLE_CATEGORY = "Category";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_CATEGORY_NAME = "name";
    private static final String COLUMN_CATEGORY_TYPE = "type";
    private static final String COLUMN_CATEGORY_IS_DELETED = "isDeleted";

    // Bảng Transaction (Đổi tên để tránh lỗi từ khóa SQLite)
    private static final String TABLE_TRANSACTION = "Transactions";
    private static final String COLUMN_TRANSACTION_ID = "transaction_id";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_TRANSACTION_TYPE = "transaction_type";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DUE_DATE = "due_date";
    private static final String COLUMN_NOTE = "note";

    // Bảng Budget
    private static final String TABLE_BUDGET = "Budget";
    private static final String COLUMN_BUDGET_ID = "budget_id";
    private static final String COLUMN_AMOUNT_LIMIT = "amount_limit";
    private static final String COLUMN_START_DATE = "start_date";
    private static final String COLUMN_END_DATE = "end_date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + TABLE_USER + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_EMAIL + " TEXT UNIQUE, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_FULL_NAME + " TEXT, "
                + COLUMN_TOKEN + " TEXT)";

        String createAccountTable = "CREATE TABLE " + TABLE_ACCOUNT + " ("
                + COLUMN_ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " INTEGER, "
                + COLUMN_ACCOUNT_NAME + " TEXT, "
                + COLUMN_BALANCE + " REAL, "
                + COLUMN_IS_DELETED + " INTEGER DEFAULT 0, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "))";

        String createCategoryTable = "CREATE TABLE " + TABLE_CATEGORY + " ("
                + COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CATEGORY_NAME + " TEXT, "
                + COLUMN_CATEGORY_TYPE + " TEXT, "
                + COLUMN_CATEGORY_IS_DELETED + " INTEGER DEFAULT 0)";

        String createTransactionTable = "CREATE TABLE " + TABLE_TRANSACTION + " ("
                + COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " INTEGER, "
                + COLUMN_ACCOUNT_ID + " INTEGER, "
                + COLUMN_CATEGORY_ID + " INTEGER, "
                + COLUMN_AMOUNT + " REAL, "
                + COLUMN_TRANSACTION_TYPE + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_DUE_DATE + " TEXT, "
                + COLUMN_NOTE + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "), "
                + "FOREIGN KEY(" + COLUMN_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNT + "(" + COLUMN_ACCOUNT_ID + "), "
                + "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_CATEGORY_ID + "))";

        String createBudgetTable = "CREATE TABLE " + TABLE_BUDGET + " ("
                + COLUMN_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " INTEGER, "
                + COLUMN_CATEGORY_ID + " INTEGER, "
                + COLUMN_AMOUNT_LIMIT + " REAL, "
                + COLUMN_START_DATE + " TEXT, "
                + COLUMN_END_DATE + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "), "
                + "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_CATEGORY_ID + "))";

        db.execSQL(createUserTable);
        db.execSQL(createAccountTable);
        db.execSQL(createCategoryTable);
        db.execSQL(createTransactionTable);
        db.execSQL(createBudgetTable);
        db.execSQL("INSERT INTO User (email,password) VALUES('thoa@gmail.com','thoa17@')");

        db.execSQL("INSERT INTO Account (user_id, name, balance, isDeleted) VALUES (1, 'Tiền mặt', 50000000, 0)");
        db.execSQL("INSERT INTO Account (user_id, name, balance, isDeleted) VALUES (1, 'Vietcombank', 10000000, 0)");
        db.execSQL("INSERT INTO Account (user_id, name, balance, isDeleted) VALUES (1, 'Momo', 20000000, 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
        onCreate(db);
    }
    public boolean registerUser(String email, String password){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("email", email);
        values.put("password", password);

        long result= db. insert("User", null, values);
        return result!=-1;
    }
    // ham kiem tra dang nhap
    public boolean checkUser(String email, String password){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM User where email=? AND password=?", new String[]{email,password});
        boolean exists =cursor.getCount()>0;
        cursor.close();
        return exists;
    }

    public List<Account> getAllAccounts() {
        List<Account> accountList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("accounts", null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double balance = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"));

                Account account = new Account(id, name, balance);
                accountList.add(account);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return accountList;
    }

    public List<Account> getAccountsByUserId(int userId) {
        List<Account> accountList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT account_id, name, balance FROM Account WHERE user_id = ? AND isDeleted = 0";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int accountId = cursor.getInt(0);
                String accountName = cursor.getString(1);
                double balance = cursor.getDouble(2);

                Account account = new Account(accountId, accountName, balance);
                accountList.add(account);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return accountList;
    }

    public long insertAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", 1);
        values.put("name", account.getName());
        values.put("balance", account.getBalance());
        values.put("isDeleted", 0);

        long result = db.insert("Account", null, values);
        db.close();
        return result;
    }
}