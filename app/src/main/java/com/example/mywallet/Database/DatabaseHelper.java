package com.example.mywallet.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mywallet.Models.Account;
import com.example.mywallet.Models.Budget;
import com.example.mywallet.Models.Category;
import com.example.mywallet.Models.Transaction;
import com.example.mywallet.R;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "finance_manager.db";
    public static final int DATABASE_VERSION = 6;

    // Bảng User
    public static final String TABLE_USER = "User";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_TOKEN = "token";

    // Bảng Account
    public static final String TABLE_ACCOUNT = "Account";
    public static final String COLUMN_ACCOUNT_ID = "account_id";
    public static final String COLUMN_ACCOUNT_NAME = "name";
    public static final String COLUMN_BALANCE = "balance";
    private static final String COLUMN_IS_DELETED = "isDeleted";

    // Bảng Category
    public static final String TABLE_CATEGORY = "Category";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_CATEGORY_NAME = "name";
    public static final String COLUMN_CATEGORY_TYPE = "type";
    public static final String COLUMN_CATEGORY_IS_DELETED = "isDeleted";

    // Bảng Transaction (Đổi tên để tránh lỗi từ khóa SQLite)
    private static final String TABLE_TRANSACTION = "Transactions";
    private static final String COLUMN_TRANSACTION_ID = "transaction_id";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DUE_DATE = "due_date";
    private static final String COLUMN_NOTE = "note";

    // Bảng Budget
    public static final String TABLE_BUDGET = "Budget";
    public static final String COLUMN_BUDGET_ID = "budget_id";
    public static final String COLUMN_BUDGET_NAME = "budget_name";
    public static final String COLUMN_AMOUNT_LIMIT = "amount_limit";
    public static final String COLUMN_AMOUNT_SPENT = "amount_spent";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";

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

        String createAccountTable = "CREATE TABLE IF NOT EXISTS " + TABLE_ACCOUNT + " ("
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
                + COLUMN_ACCOUNT_ID + " INTEGER, "
                + COLUMN_BUDGET_NAME + " TEXT, "
                + COLUMN_AMOUNT_LIMIT + " REAL, "
                + COLUMN_AMOUNT_SPENT + " REAL DEFAULT 0, "
                + COLUMN_START_DATE + " TEXT, "
                + COLUMN_END_DATE + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "), "
                + "FOREIGN KEY(" + COLUMN_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNT + "(" + COLUMN_ACCOUNT_ID + "), "
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

        //Thêm danh mục
        db.execSQL("INSERT INTO Category (name, type) VALUES ('Ăn uống', 'Chi')");
        db.execSQL("INSERT INTO Category (name, type) VALUES ('Mua sắm', 'Chi')");
        db.execSQL("INSERT INTO Category (name, type) VALUES ('Giải trí', 'Chi')");
        db.execSQL("INSERT INTO Category (name, type) VALUES ('Tiền lương', 'Thu')");
        db.execSQL("INSERT INTO Category (name, type) VALUES ('Tiền thưởng', 'Thu')");
        db.execSQL("INSERT INTO Category (name, type) VALUES ('Tiền cấp', 'Thu')");

        db.execSQL("INSERT INTO Transactions (user_id, account_id, category_id, amount, date, due_date, note) " +
                "VALUES (1, 1, 1, 100000, '2025-03-01', NULL, 'Ăn sáng')");
        db.execSQL("INSERT INTO Transactions (user_id, account_id, category_id, amount, date, due_date, note) " +
                "VALUES (1, 2, 2, 300000, '2025-03-02', NULL, 'Mua sách')");
        db.execSQL("INSERT INTO Transactions (user_id, account_id, category_id, amount, date, due_date, note) " +
                "VALUES (1, 3, 3, 200000, '2025-03-03', NULL, 'Xem phim')");
        db.execSQL("INSERT INTO Transactions (user_id, account_id, category_id, amount, date, due_date, note) " +
                "VALUES (1, 1, 4, 5000000, '2025-03-04', NULL, 'Lương tháng 3')");
        db.execSQL("INSERT INTO Transactions (user_id, account_id, category_id, amount, date, due_date, note) " +
                "VALUES (1, 1, 5, 1000000, '2025-03-05', NULL, 'Thưởng dự án')");
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

        Cursor cursor = db.query(TABLE_ACCOUNT, null, null, null, null, null, null);

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

    //Lấy danh mục theo loại
    public List<Category> getCategoriesByType(String type) {
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CATEGORY + " WHERE "
                + COLUMN_CATEGORY_TYPE + " = ? AND " + COLUMN_CATEGORY_IS_DELETED + " = 0";
        Cursor cursor = db.rawQuery(query, new String[]{type});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME));
                String catType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_TYPE));
                // Set lại id của icon mặc định
                int icon = R.drawable.account;
                Category category = new Category(id, icon, name, catType);
                categoryList.add(category);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return categoryList;
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

    //Lấy tất cả tài khoản chưa bị xóa
    public List<Account> getAllAccounts_NonDeleted() {
        List<Account> accountList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ACCOUNT + " WHERE " + COLUMN_IS_DELETED + " = 0";  // Only non-deleted accounts
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Account account = new Account();
                account.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_ID)));
                account.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_NAME)));
                account.setBalance(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BALANCE)));
                accountList.add(account);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return accountList;
    }
    //Lấy tất cả danh mục chưa bị xóa
    public List<Category> getAllCategories_NonDeleted() {
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + COLUMN_CATEGORY_IS_DELETED + " = 0";  // Only non-deleted categories
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
                category.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)));
                category.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_TYPE)));
                categoryList.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categoryList;
    }
    //Lấy tất cả ngân sách
    public List<Budget> getAllBudgets(int userId) {
        List<Budget> budgetList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn tất cả ngân sách cho người dùng
        String query = "SELECT * FROM " + TABLE_BUDGET + " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        // Duyệt qua tất cả các bản ghi và tạo đối tượng Budget
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int budgetId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BUDGET_ID));
                String budgetName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BUDGET_NAME));
                double amountLimit = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT_LIMIT));
                double amountSpent = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT_SPENT));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE));
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID));

                // Tạo đối tượng Budget và thêm vào danh sách
                Budget budget = new Budget(budgetId, budgetName, amountLimit, amountSpent, startDate, endDate, categoryId);
                budgetList.add(budget);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return budgetList;
    }
    // Xóa ngân sách với ID đã cho
    public boolean deleteBudget(int budgetId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_BUDGET, COLUMN_BUDGET_ID + " = ?", new String[]{String.valueOf(budgetId)});

        return rowsAffected > 0; // Trả về true nếu xóa thành công
    }

    //Thêm dữ liệu vào bảng Ngân Sách
    public long insertBudget(int userId, int categoryId, int accountId, String budgetName, double amountLimit, String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("user_id", userId);
        values.put("category_id", categoryId);
        values.put("account_id", accountId);
        values.put("budget_name", budgetName);
        values.put("amount_limit", amountLimit);
        values.put("amount_spent", 0);
        values.put("start_date", startDate);
        values.put("end_date", endDate);

        long result = db.insert("Budget", null, values);
        db.close();

        return result;
    }

    //Trừ tiền khi thêm ngân sách mới
    public void deductBalance(int accountId, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_ACCOUNT +
                " SET " + COLUMN_BALANCE + " = " + COLUMN_BALANCE + " - ?" +
                " WHERE " + COLUMN_ACCOUNT_ID + " = ?";
        db.execSQL(updateQuery, new Object[]{amount, accountId});
    }
    //Sửa ngân sách theo budgetId
    public boolean updateBudget(int budgetId, int userId, int categoryId, String budgetName, double amountLimit, double amountSpent, String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_USER_ID, userId);
        contentValues.put(COLUMN_CATEGORY_ID, categoryId);
        contentValues.put(COLUMN_BUDGET_NAME, budgetName);
        contentValues.put(COLUMN_AMOUNT_LIMIT, amountLimit);
        contentValues.put(COLUMN_AMOUNT_SPENT, amountSpent);
        contentValues.put(COLUMN_START_DATE, startDate);
        contentValues.put(COLUMN_END_DATE, endDate);

        int result = db.update(TABLE_BUDGET, contentValues, COLUMN_BUDGET_ID + " = ?", new String[]{String.valueOf(budgetId)});
        return result > 0; // Trả về true nếu cập nhật thành công
    }

    //Cập nhật số tiền đã sử dụng trong Ngân sách thuộc 1 danh mục
    public void updateBudgetSpent(int categoryId, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_BUDGET + " SET " + COLUMN_AMOUNT_SPENT + " = " + COLUMN_AMOUNT_SPENT + " + ? WHERE " + COLUMN_CATEGORY_ID + " = ?";
        db.execSQL(query, new Object[]{amount, categoryId});
        db.close();
    }

    public boolean insertCategory(String name, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("type", type);
        values.put("isDeleted", 0);

        long result = db.insert("Category", null, values);
        db.close();
        return result != -1; // Nếu `result != -1` thì chèn thành công
    }

    public boolean deleteAccount(int accountId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isDeleted", 1);

        int updatedRows = db.update("Account", values, "account_id = ?", new String[]{String.valueOf(accountId)});
        db.close();
        return updatedRows > 0;
    }

    public boolean updateAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", account.getName());
        values.put("balance", account.getBalance());

        int rowsAffected = db.update("Account", values, "account_id = ?", new String[]{String.valueOf(account.getId())});
        db.close();
        return rowsAffected > 0;
    }
    public List<Transaction> getIncomeTransactions(String startDate, String endDate) {
        return getTransactionsByCategoryType("Thu", startDate, endDate);
    }

    public List<Transaction> getExpenseTransactions(String startDate, String endDate) {
        return getTransactionsByCategoryType("Chi", startDate, endDate);
    }

    private List<Transaction> getTransactionsByCategoryType(String type, String startDate, String endDate) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Xây dựng câu truy vấn SQL
        String query = "SELECT c.name as category_name, t.amount, t.date, t.note " +
                "FROM Transactions t " +
                "INNER JOIN Category c ON t.category_id = c.category_id " +
                "WHERE c.type = ?";

        List<String> params = new ArrayList<>();
        params.add(type);

        // Kiểm tra và thêm điều kiện cho ngày
        if (startDate != null && !startDate.isEmpty()) {
            query += " AND t.date >= ?";
            params.add(startDate);
        }

        if (endDate != null && !endDate.isEmpty()) {
            query += " AND t.date <= ?";
            params.add(endDate);
        }

        // Thực thi câu truy vấn SQL
        Cursor cursor = db.rawQuery(query, params.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(
                        cursor.getString(cursor.getColumnIndexOrThrow("category_name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                        cursor.getString(cursor.getColumnIndexOrThrow("note")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date"))
                );
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return transactions;
    }

    public boolean updateCategory(int categoryId, String newName, String newType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, newName);
        values.put(COLUMN_CATEGORY_TYPE, newType);

        int rowsAffected = db.update(TABLE_CATEGORY, values, COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(categoryId)});
        db.close();
        return rowsAffected > 0; // Trả về true nếu cập nhật thành công
    }

    public boolean deleteCategory(int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isDeleted", 1);  // Đánh dấu là đã xoá

        int rowsAffected = db.update(TABLE_CATEGORY, values, COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(categoryId)});
        db.close();
        return rowsAffected > 0; // Trả về true nếu xoá thành công
    }



}