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
import com.github.mikephil.charting.data.PieEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "finance_manager.db";
    public static final int DATABASE_VERSION = 14;

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
        db.execSQL("INSERT INTO Category (name, type) VALUES ('Khoản cho vay', 'Chi')");

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
        db.execSQL("INSERT INTO Budget (user_id,category_id,amount_limit,start_date,end_date) VALUES('1','1',1000000,'2025-1-1','2025-2-2')");
        db.execSQL("INSERT INTO Budget (user_id,category_id,amount_limit,start_date,end_date) VALUES('1','2',100000,'2025-1-1','2025-2-2')");
        db.execSQL("INSERT INTO Budget (user_id,category_id,amount_limit,start_date,end_date) VALUES('1','3',100000,'2025-1-1','2025-2-2')");
        db.execSQL("INSERT INTO Budget (user_id,category_id,amount_limit,start_date,end_date) VALUES('1','7',1000000,'2025-1-1','2025-4-24')");
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
        String query = "SELECT t.transaction_id, c.name as category_name, t.amount, t.date, t.note " +
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

        // Kiểm tra kết quả truy vấn
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Kiểm tra transaction_id có tồn tại trong cursor hay không
                int transactionId = cursor.getInt(cursor.getColumnIndexOrThrow("transaction_id"));
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                // Tạo transaction mới và thêm vào danh sách
                Transaction transaction = new Transaction(transactionId, categoryName, amount, note, date);
                transactions.add(transaction);
            } while (cursor.moveToNext());
        } else {
            Log.d("DatabaseHelper", "No transactions found.");
        }

        // Đóng con trỏ và cơ sở dữ liệu
        if (cursor != null) {
            cursor.close();
        }
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
    public List<String> getAccountsByUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> accounts = new ArrayList<>();

        String query = "SELECT name FROM " + TABLE_ACCOUNT + " WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null) {
            // Kiểm tra cột 'name' có tồn tại không
            int nameColumnIndex = cursor.getColumnIndex("name");

            // Nếu cột 'name' không tồn tại, trả về một danh sách trống
            if (nameColumnIndex == -1) {
                Log.e("DB_ERROR", "Cột 'name' không tồn tại trong bảng Account.");
                cursor.close();
                return accounts;
            }

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameColumnIndex);
                accounts.add(name);
            }
            cursor.close();
        }
        return accounts;
    }
    public List<String> getCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> categories = new ArrayList<>();

        String query = "SELECT name FROM " + TABLE_CATEGORY;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            // Check if the column index is valid before using it
            int columnIndex = cursor.getColumnIndex("name");

            if (columnIndex >= 0) {  // Ensure the column exists
                while (cursor.moveToNext()) {
                    String name = cursor.getString(columnIndex);
                    categories.add(name);
                }
            } else {
                // Handle the case where the column doesn't exist (optional)
                Log.e("getCategories", "Column 'name' not found in table.");
            }
            cursor.close();
        }
        return categories;
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
    public boolean insertTransaction(int userId, int accountId, int categoryId, double amount, String dueDate, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_ACCOUNT_ID, accountId);
        values.put(COLUMN_CATEGORY_ID, categoryId);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())); // Lấy ngày hiện tại
        values.put(COLUMN_DUE_DATE, dueDate);
        values.put(COLUMN_NOTE, note);
        long result = db.insert(TABLE_TRANSACTION, null, values);
        db.close();
        return result != -1;
    }
    public double getRemainingBudget(int userId, int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Tính tổng số tiền đã chi cho danh mục này
        String query = "SELECT SUM(amount) FROM " + TABLE_TRANSACTION +
                " WHERE user_id = ? AND category_id = ?";  // Truy vấn tổng tiền đã chi
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(categoryId)});

        double totalSpent = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                totalSpent = cursor.getDouble(0);  // Tổng số tiền đã chi
            }
            cursor.close();
        }

        // Lấy ngân sách của danh mục từ bảng BUDGET (thay vì từ TABLE_CATEGORY)
        double budget = 0;  // Nếu không có ngân sách, mặc định là 0
        String budgetQuery = "SELECT amount_limit FROM " + TABLE_BUDGET +
                " WHERE user_id = ? AND category_id = ?";  // Truy vấn ngân sách theo userId và categoryId
        Cursor budgetCursor = db.rawQuery(budgetQuery, new String[]{String.valueOf(userId), String.valueOf(categoryId)});

        if (budgetCursor != null) {
            int columnIndex = budgetCursor.getColumnIndex("amount_limit");
            if (columnIndex != -1 && budgetCursor.moveToFirst()) {
                budget = budgetCursor.getDouble(columnIndex);  // Ngân sách của danh mục
            }
            budgetCursor.close();
        }
        Log.d("DEBUG", "Budget: " + budget);
        Log.d("DEBUG", "Total Spent: " + totalSpent);


        return budget - totalSpent;  // Tính số tiền còn lại
    }
    public double getTotalBudget(int userId) {
        double totalBudget = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(amount_limit) FROM Budget WHERE user_id = ? " +
                "AND start_date <= date('now') AND end_date >= date('now')";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            totalBudget = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return totalBudget;
    }
    public double[] getIncomeExpense(int userId) {
        double[] result = new double[2]; // [0] = chi tiêu, [1] = thu nhập
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " +
                "(SELECT COALESCE(SUM(t.amount), 0) " +
                " FROM Transactions t " +
                " JOIN Category c ON t.category_id = c.category_id " +
                " WHERE t.user_id = ? " +
                " AND t.date >= date('now', 'start of month') " +
                " AND t.date <= date('now') " +
                " AND c.type = 'Chi') AS total_expense, " +

                "(SELECT COALESCE(SUM(t.amount), 0) " +
                " FROM Transactions t " +
                " JOIN Category c ON t.category_id = c.category_id " +
                " WHERE t.user_id = ? " +
                " AND t.date >= date('now', 'start of month') " +
                " AND t.date <= date('now') " +
                " AND c.type = 'Thu') AS total_income";

        Log.d("SQL_QUERY", "Query: " + query); // In truy vấn ra log

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                result[0] = cursor.getDouble(0); // Tổng chi tiêu
                result[1] = cursor.getDouble(1); // Tổng thu nhập
                Log.d("SQL_RESULT", "Chi tiêu: " + result[0] + ", Thu nhập: " + result[1]);
            } else {
                Log.e("SQL_ERROR", "Không có dữ liệu trả về!");
            }
        } catch (Exception e) {
            Log.e("SQL_EXCEPTION", "Lỗi truy vấn: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return result;
    }
    public double getTotalBalance(int userId) {
        double totalBalance = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(balance) FROM Account WHERE user_id = ? AND isDeleted = 0";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            totalBalance = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return totalBalance;
    }
    public void deleteTransaction(int transactionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Transactions", "transaction_id = ?", new String[]{String.valueOf(transactionId)});
        db.close();
    }
    public boolean updateTransaction(int transactionId, double amount, String date, String dueDate, String note, int accountId, int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("date", date);
        values.put("due_date", dueDate);
        values.put("note", note);
        values.put("account_id", accountId);
        values.put("category_id", categoryId);

        int rows = db.update("Transactions", values, "transaction_id = ?", new String[]{String.valueOf(transactionId)});
        return rows > 0;
    }
    public Transaction getTransactionById(int transactionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Transaction transaction = null;

        Cursor cursor = db.rawQuery("SELECT * FROM Transactions WHERE transaction_id = ?", new String[]{String.valueOf(transactionId)});

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("transaction_id");
            int userIdIndex = cursor.getColumnIndex("user_id");
            int accountIdIndex = cursor.getColumnIndex("account_id");
            int categoryIdIndex = cursor.getColumnIndex("category_id");
            int amountIndex = cursor.getColumnIndex("amount");
            int dateIndex = cursor.getColumnIndex("date");
            int dueDateIndex = cursor.getColumnIndex("due_date");
            int noteIndex = cursor.getColumnIndex("note");

            if (idIndex != -1 && userIdIndex != -1 && accountIdIndex != -1 &&
                    categoryIdIndex != -1 && amountIndex != -1 && dateIndex != -1 &&
                    dueDateIndex != -1 && noteIndex != -1) {

                transaction = new Transaction(
                        cursor.getInt(idIndex),
                        cursor.getInt(userIdIndex),
                        cursor.getInt(accountIdIndex),
                        cursor.getInt(categoryIdIndex),
                        cursor.getDouble(amountIndex),
                        cursor.getString(dateIndex),
                        cursor.getString(dueDateIndex),
                        cursor.getString(noteIndex)
                );
                Log.d("DatabaseQuery", "Lấy giao dịch thành công: ID = " + transaction.getTransactionId());
            } else {
                Log.e("DatabaseError", "Một hoặc nhiều cột không tồn tại!");
            }
        } else {
            Log.e("DatabaseError", "Không tìm thấy giao dịch với ID: " + transactionId);
        }

        cursor.close();
        return transaction;
    }
    public String getAccountNameById(int accountId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String accountName = null;

        try {
            cursor = db.query("Account", // Tên bảng tài khoản
                    new String[]{"name"}, // Cột cần lấy là tên tài khoản
                    "account_id = ?", // Điều kiện truy vấn (id tài khoản)
                    new String[]{String.valueOf(accountId)}, // Tham số truy vấn
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("name");
                if (columnIndex >= 0) {
                    accountName = cursor.getString(columnIndex);
                } else {
                    Log.e("getAccountNameById", "Cột 'name' không tồn tại.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return accountName;
    }

    public String getCategoryNameById(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String categoryName = null;

        try {
            cursor = db.query("Category", // Tên bảng danh mục
                    new String[]{"name"}, // Cột cần lấy là tên danh mục
                    "category_id = ?", // Điều kiện truy vấn (id danh mục)
                    new String[]{String.valueOf(categoryId)}, // Tham số truy vấn
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("name");
                if (columnIndex >= 0) {
                    categoryName = cursor.getString(columnIndex);
                } else {
                    Log.e("getCategoryNameById", "Cột 'name' không tồn tại.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return categoryName;
    }





    public ArrayList<PieEntry> getIncomeData(SQLiteDatabase db, String startDate, String endDate) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        String query = "SELECT category.name, SUM(Transactions.amount) as total " +
                "FROM Transactions " +
                "INNER JOIN category ON Transactions.category_id = category.id " +
                "WHERE category.type = 'Thu'";

        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            query += " AND Transactions.date BETWEEN ? AND ?";
        }

        query += " GROUP BY category.name";

        Cursor cursor;
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            cursor = db.rawQuery(query, new String[]{startDate, endDate});
        } else {
            cursor = db.rawQuery(query, null);
        }

        while (cursor.moveToNext()) {
            String categoryName = cursor.getString(0);
            float totalAmount = cursor.getFloat(1);
            entries.add(new PieEntry(totalAmount, categoryName));
        }
        cursor.close();
        return entries;
    }
}