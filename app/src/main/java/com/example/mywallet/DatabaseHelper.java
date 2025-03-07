package com.example.mywallet;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "finance_managers.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;




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
       /*db.execSQL("INSERT INTO User (email,password) VALUES('thoa@gmail.com','thoa17@')");
       db.execSQL("INSERT INTO User (email,password) VALUES('maianh@gmail.com','thoa12@')");
       db.execSQL("INSERT INTO Account (account_id,name,balance,isDeleted) VALUES(1,'Ngân hàng Vietcombank',12000000,0)");
       db.execSQL("INSERT INTO Category (name,type,isDeleted) VALUES('Ăn uống','Chi',0)");
       db.execSQL("INSERT INTO Category (name,type,isDeleted) VALUES('Xăng xe','Chi',0)");*/
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
        while (cursor.moveToNext()) {
            String dbEmail = cursor.getString(0);
            String dbPassword = cursor.getString(1);
            Log.d("DB_CHECK", "Email: " + dbEmail + ", Password: " + dbPassword);
        }
        cursor.close();
        return exists;
    }
    public boolean insertTransaction(int userId, int accountId, int categoryId, double amount, String type, String date, String dueDate, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("user_id", userId);
        values.put("account_id", accountId);
        values.put("category_id", categoryId);
        values.put("amount", amount);
        values.put("transaction_type", type);
        values.put("date", date);
        values.put("due_date", dueDate); // Bổ sung thiếu sót
        values.put("note", note);

        long result = db.insert("Transactions", null, values);
        db.close();
        return result != -1;
    }


    public boolean updateTransaction(int transactionId, int accountId, int categoryId, double amount, String type, String date, String dueDate, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("account_id", accountId);
        values.put("category_id", categoryId);
        values.put("amount", amount);
        values.put("transaction_type", type);
        values.put("date", date);
        values.put("due_date", dueDate); // ✅ Bổ sung due_date
        values.put("note", note);

        int rowsAffected = db.update("Transactions", values, "id = ?", new String[]{String.valueOf(transactionId)});
        db.close();
        return rowsAffected > 0;
    }







    // Hàm lấy ID của danh mục từ tên danh mục
    private int getCategoryId(String categoryName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_CATEGORY_ID + " FROM " + TABLE_CATEGORY + " WHERE " + COLUMN_CATEGORY_NAME + "=?", new String[]{categoryName});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        cursor.close();
        return -1; // Trả về -1 nếu không tìm thấy
    }
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM Category WHERE isDeleted = 0", null);


        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categories;
    }
    public int getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1); // Đúng key đã lưu
        Log.d("DB_USER", "User ID lấy được: " + userId);
        return userId;
    }
    public Map<String, String> getTransactionById(int transactionId) {
        Map<String, String> transaction = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT c.name, t.amount, t.transaction_type, t.date, t.due_date, t.note, a.name " +
                "FROM Transactions t " +
                "JOIN Category c ON t.category_id = c.category_id " + // Lấy tên danh mục
                "JOIN Account a ON t.account_id = a.account_id " +    // 🔥 Thêm JOIN để lấy payment_method
                "WHERE t.transaction_id = ?";



        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(transactionId)});
        Log.d("DB_QUERY", "Số giao dịch lấy được: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            transaction.put("category_name", cursor.getString(0)); // Tên danh mục
            transaction.put("amount", cursor.getString(1)); // Số tiền
            transaction.put("transaction_type", cursor.getString(2)); // Loại giao dịch
            transaction.put("date", cursor.getString(3)); // Ngày giao dịch
            transaction.put("due_date", cursor.getString(4)); // Hạn thanh toán
            transaction.put("note", cursor.getString(5)); // Ghi chú
            transaction.put("payment_method", cursor.getString(6)); // ✅ Lấy phương thức thanh toán từ bảng Account

            Log.d("DB_QUERY", "Giao dịch: " + transaction.toString());
        } else {
            Log.d("DB_QUERY", "Không tìm thấy giao dịch với ID: " + transactionId);
        }

        cursor.close();
        db.close();
        return transaction;
    }


    public List<Map<String, String>> getTransactionsForCurrentUser(Context context) {
        List<Map<String, String>> transactions = new ArrayList<>();
        int user_id = getUserId(context);

        if (user_id == -1) {
            Log.d("DB_QUERY", "Không có user đăng nhập.");
            return transactions; // Không có user đăng nhập
        }

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT t.transaction_id, t.account_id, c.name, t.amount, t.transaction_type, t.date, t.due_date, t.note " +
                "FROM Transactions t " +
                "JOIN Category c ON t.category_id = c.category_id " +
                "WHERE t.user_id = ?";


        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(user_id)});
        Log.d("DB_QUERY", "Số giao dịch lấy được: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                Map<String, String> transaction = new HashMap<>();
                transaction.put("transaction_id", cursor.getString(0)); // Thêm transaction_id
                transaction.put("category_name", cursor.getString(2)); // Tên danh mục
                transaction.put("amount", cursor.getString(3)); // Số tiền
                transaction.put("transaction_type", cursor.getString(4)); // Loại giao dịch
                transaction.put("date", cursor.getString(5)); // Ngày giao dịch
                transaction.put("due_date", cursor.getString(6)); // Hạn thanh toán
                transaction.put("note", cursor.getString(7)); // Ghi chú
                Log.d("DB_QUERY", "Giao dịch: " + transaction.toString());

                transactions.add(transaction);
            } while (cursor.moveToNext());
        }
        else {
            Log.d("DB_QUERY", "Không có giao dịch nào trong database.");
        }

        cursor.close();
        db.close();
        return transactions;
    }




    public int getCategoryIdByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT category_id FROM Category WHERE name = ?", new String[]{name});
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return id;
    }


    public int getAccountIdByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT account_id FROM Account WHERE name = ?", new String[]{name});
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return id;
    }


    public List<String> getAllBudgets() {
        List<String> budgets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM Account WHERE isDeleted = 0", null);


        if (cursor.moveToFirst()) {
            do {
                budgets.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return budgets;
    }


    public void checkDatabase() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM User", null);


            if (cursor != null && cursor.getCount() == 0) {
                Log.e("DatabaseCheck", "Bảng User không có dữ liệu!");
            } else if (cursor != null) {
                // Kiểm tra xem các cột có tồn tại trong bảng hay không
                int emailColumnIndex = cursor.getColumnIndex(COLUMN_EMAIL);
                int passwordColumnIndex = cursor.getColumnIndex(COLUMN_PASSWORD);


                if (emailColumnIndex == -1 || passwordColumnIndex == -1) {
                    Log.e("DatabaseCheckError", "Một hoặc nhiều cột không tồn tại trong bảng User");
                } else {
                    // Di chuyển con trỏ đến các dòng dữ liệu
                    while (cursor.moveToNext()) {
                        String email = cursor.getString(emailColumnIndex);
                        String password = cursor.getString(passwordColumnIndex);
                        Log.i("DatabaseCheck", "Dữ liệu trong User - Email: " + email + ", Password: " + password);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseCheckError", "Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }
    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT user_id FROM User WHERE email = ?", new String[]{email});
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return userId;
    }






}
