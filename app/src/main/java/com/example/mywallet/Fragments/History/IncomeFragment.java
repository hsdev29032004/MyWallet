package com.example.mywallet.Fragments.History;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mywallet.Database.DatabaseHelper;
import com.example.mywallet.Models.Transaction;
import com.example.mywallet.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncomeFragment extends Fragment {
    private EditText edtStartDate, edtEndDate;
    private Button btnSearch, btnExportPDF;
    private LinearLayout layoutIncomeHistory;
    private PieChart pieChartIncome;
    private DatabaseHelper dbHelper;
    private boolean isSearchPerformed = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        edtStartDate = view.findViewById(R.id.edtStartDate);
        edtEndDate = view.findViewById(R.id.edtEndDate);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnExportPDF = view.findViewById(R.id.btnExportPDF);
        layoutIncomeHistory = view.findViewById(R.id.layoutIncomeHistory);
        pieChartIncome = view.findViewById(R.id.pieChartIncome);

        dbHelper = new DatabaseHelper(getContext());

        edtStartDate.setOnClickListener(v -> showDatePicker(edtStartDate));
        edtEndDate.setOnClickListener(v -> showDatePicker(edtEndDate));

        loadIncomeTransactions("", "");

        btnSearch.setOnClickListener(v -> {
            String startDate = edtStartDate.getText().toString();
            String endDate = edtEndDate.getText().toString();
            loadIncomeTransactions(startDate, endDate);
        });

        btnExportPDF.setOnClickListener(v -> {
            if (!isSearchPerformed) {
                Toast.makeText(getContext(), "Không có giao dịch để xuất báo cáo!", Toast.LENGTH_SHORT).show();
                return;
            }
            exportToPDF();
        });



        return view;
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String date = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    editText.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void loadIncomeTransactions(String startDate, String endDate) {
        List<Transaction> incomeTransactions = dbHelper.getIncomeTransactions(startDate, endDate);
        layoutIncomeHistory.removeAllViews();
        if (incomeTransactions.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy giao dịch nào!", Toast.LENGTH_SHORT).show();
            isSearchPerformed = false; // Vẫn chưa có dữ liệu hợp lệ để xuất PDF
            return;
        }

        isSearchPerformed = true; // Đánh dấu đã tìm kiếm thành công

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (Transaction transaction : incomeTransactions) {
            View itemView = inflater.inflate(R.layout.item_transaction, layoutIncomeHistory, false);

            TextView tvCategory = itemView.findViewById(R.id.tvCategory);
            TextView tvAmount = itemView.findViewById(R.id.tvAmount);
            TextView tvNote = itemView.findViewById(R.id.tvNote);
            TextView tvDate = itemView.findViewById(R.id.tvDate);
            ImageButton btnDelete = itemView.findViewById(R.id.btnDelete); // Nút xóa
            ImageButton btnEdit = itemView.findViewById(R.id.btnEdit);

            tvCategory.setText(transaction.getCategoryName());
            tvAmount.setText(String.valueOf(transaction.getAmount()));
            tvNote.setText(transaction.getNote());
            tvDate.setText(transaction.getDate());

            // Xử lý sự kiện khi nhấn nút xóa
            btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog(transaction.getTransactionId(), startDate, endDate));
            // Xử lý sự kiện sửa
            btnEdit.setOnClickListener(v -> {
                showEditTransactionDialog(transaction, startDate, endDate);
            });


            layoutIncomeHistory.addView(itemView);
        }
        loadIncomeChart(incomeTransactions);
        // Cập nhật UI ngay trên main thread
        getActivity().runOnUiThread(() -> layoutIncomeHistory.invalidate());
    }
    private void showEditTransactionDialog(Transaction transaction, String startDate, String endDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chỉnh sửa giao dịch");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_transaction, null);
        EditText edtAmount = view.findViewById(R.id.edtAmount);
        EditText edtNote = view.findViewById(R.id.edtNote);
        EditText edtDate = view.findViewById(R.id.edtDate);
        Spinner spnCategory = view.findViewById(R.id.spnCategory);

        edtAmount.setText(String.valueOf(transaction.getAmount()));
        edtNote.setText(transaction.getNote());
        edtDate.setText(transaction.getDate());

        // Lấy danh sách danh mục từ database
        Map<String, Integer> categoriesMap = dbHelper.getAllCategoriesWithIds();
        List<String> categoryNames = new ArrayList<>(categoriesMap.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, categoryNames);
        spnCategory.setAdapter(adapter);

        // Chọn danh mục hiện tại
        int categoryIndex = categoryNames.indexOf(transaction.getCategoryName());
        if (categoryIndex != -1) {
            spnCategory.setSelection(categoryIndex);
        }

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            try {
                double newAmount = Double.parseDouble(edtAmount.getText().toString());
                String newNote = edtNote.getText().toString();
                String selectedCategory = spnCategory.getSelectedItem().toString();
                int categoryId = categoriesMap.get(selectedCategory); // Lấy ID danh mục

                // Cập nhật vào database và kiểm tra kết quả
                boolean isUpdated = dbHelper.updateTransaction(transaction.getTransactionId(), newAmount, newNote, categoryId);

                if (isUpdated) {
                    // Nếu sửa thành công, cập nhật ngay trên giao diện
                    transaction.setAmount(newAmount);
                    transaction.setNote(newNote);
                    transaction.setCategoryName(selectedCategory);

                    // Cập nhật lại mục giao dịch trong layout
                    updateTransactionView(transaction);

                    // Hiển thị thông báo thành công
                    Toast.makeText(getContext(), "Cập nhật giao dịch thành công", Toast.LENGTH_SHORT).show();

                    // Làm mới lại danh sách giao dịch (cập nhật UI)
                    loadIncomeTransactions("", "");
                } else {
                    // Nếu có lỗi xảy ra, thông báo lỗi
                    Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // Bắt lỗi nếu có và hiển thị thông báo
                e.printStackTrace();
                Toast.makeText(getContext(), "Đã xảy ra lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });



        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void updateTransactionView(Transaction transaction) {
        for (int i = 0; i < layoutIncomeHistory.getChildCount(); i++) {
            View itemView = layoutIncomeHistory.getChildAt(i);
            TextView tvCategory = itemView.findViewById(R.id.tvCategory);
            TextView tvAmount = itemView.findViewById(R.id.tvAmount);
            TextView tvNote = itemView.findViewById(R.id.tvNote);

            // Kiểm tra nếu đây là giao dịch đang sửa và cập nhật lại
            if (tvCategory.getText().toString().equals(transaction.getCategoryName()) &&
                    tvAmount.getText().toString().equals(String.valueOf(transaction.getAmount())) &&
                    transaction.getTransactionId() == Integer.parseInt(tvCategory.getTag().toString())) {
                tvCategory.setText(transaction.getCategoryName());
                tvAmount.setText(String.valueOf(transaction.getAmount()));
                tvNote.setText(transaction.getNote());
                break;
            }
        }
    }


    private void showDeleteConfirmationDialog(int transactionId, String startDate, String endDate) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa giao dịch này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbHelper.deleteTransaction(transactionId);
                    loadIncomeTransactions(startDate, endDate); // Cập nhật danh sách
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void loadIncomeChart(List<Transaction> transactions) {
        Map<String, Double> categoryTotals = new HashMap<>();

        for (Transaction transaction : transactions) {
            String categoryName = transaction.getCategoryName();
            double amount = transaction.getAmount();

            categoryTotals.put(categoryName, categoryTotals.getOrDefault(categoryName, 0.0) + amount);
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Thu nhập");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(14f);

        PieData pieData = new PieData(dataSet);

        pieChartIncome.setData(pieData);
        pieChartIncome.setCenterText("Thu nhập theo danh mục");
        pieChartIncome.animateY(1000);
    }

    //CODE CHO XUẤT FILE PDF
    public void exportToPDF() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        int pageWidth = 595; // Kích thước chuẩn A4
        int pageHeight = 842;
        int yPosition = 50;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Tiêu đề báo cáo
        paint.setTextSize(20);
        canvas.drawText("Báo cáo tài chính cá nhân", 180, yPosition, paint);
        yPosition += 40;

        // Chụp ảnh biểu đồ PieChart
        Bitmap bitmap = getChartBitmap(pieChartIncome);
        if (bitmap != null) {
            // Scale hình ảnh biểu đồ để vừa trang PDF
            int scaledWidth = pageWidth - 100;
            int scaledHeight = (int) ((float) bitmap.getHeight() / bitmap.getWidth() * scaledWidth);
            canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true), 50, yPosition, paint);
            yPosition += scaledHeight + 20;
        }

        // Thêm lịch sử giao dịch
        paint.setTextSize(16);
        canvas.drawText("Lịch sử giao dịch:", 50, yPosition, paint);
        yPosition += 30;

        List<Transaction> transactions = dbHelper.getIncomeTransactions("", "");
        for (Transaction transaction : transactions) {
            if (yPosition > pageHeight - 50) {
                pdfDocument.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.getPages().size() + 1).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                yPosition = 50;
            }
            String transactionText = transaction.getDate() + " - " + transaction.getCategoryName() + ": " + transaction.getAmount();
            canvas.drawText(transactionText, 50, yPosition, paint);
            yPosition += 20;
        }

        pdfDocument.finishPage(page);

        // Lưu PDF vào thư mục Download
        ContentResolver contentResolver = getContext().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "income_report.pdf");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
        }

        Uri uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues);

        try {
            OutputStream outputStream = contentResolver.openOutputStream(uri);
            pdfDocument.writeTo(outputStream);
            pdfDocument.close();
            outputStream.close();
            openPDF(uri); // Mở file PDF sau khi lưu thành công
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Chụp ảnh biểu đồ Pie Chart
    public Bitmap getChartBitmap(PieChart chart) {
        chart.measure(View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(600, View.MeasureSpec.EXACTLY));
        chart.layout(0, 0, chart.getMeasuredWidth(), chart.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(chart.getWidth(), chart.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        chart.draw(canvas);

        return bitmap;
    }

    //Mở file sau khi lưu
    public void openPDF(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    //Cập nhật PieChart và đảm bảo nó hiển thị đúng, ko bị lệch
    @Override
    public void onResume() {
        super.onResume();
        if (pieChartIncome != null) {
            ViewGroup.LayoutParams params = pieChartIncome.getLayoutParams();
            params.height = 900; // Đặt lại chiều cao ban đầu
            pieChartIncome.setLayoutParams(params);
            pieChartIncome.invalidate(); // Cập nhật lại PieChart
        }
    }


}