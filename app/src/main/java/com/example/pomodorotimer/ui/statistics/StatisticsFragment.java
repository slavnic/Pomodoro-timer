package com.example.pomodorotimer.ui.statistics;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.example.pomodorotimer.R;
import com.example.pomodorotimer.data.HandlerDB;

// PDF imports
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private CalendarView calendarView;
    private TextView tvTotalMinutes, tvSessionCount, tvBreakTime, tvSelectedDate;
    private TextView tvMonthlyWork, tvMonthlyBreak, tvMonthlySessions;
    private LinearLayout dailyStatsLayout, monthlyStatsLayout;
    private Button btnDailyStats, btnMonthlyStats, btnExportCSV;
    private HandlerDB handlerDB;

    private boolean isDailyView = true;

    private String contentToSave;
    private String fileExtension;
    private byte[] pdfBytesToSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        initViews(view);
        setupCalendar();
        setupButtons();

        try {
            handlerDB = HandlerDB.getInstance();
            loadTodayStatistics();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void initViews(View view) {
        calendarView = view.findViewById(R.id.calendarView);
        tvTotalMinutes = view.findViewById(R.id.tvTotalMinutes);
        tvSessionCount = view.findViewById(R.id.tvSessionCount);
        tvBreakTime = view.findViewById(R.id.tvBreakTime);
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);

        tvMonthlyWork = view.findViewById(R.id.tvMonthlyWork);
        tvMonthlyBreak = view.findViewById(R.id.tvMonthlyBreak);
        tvMonthlySessions = view.findViewById(R.id.tvMonthlySessions);

        dailyStatsLayout = view.findViewById(R.id.dailyStatsLayout);
        monthlyStatsLayout = view.findViewById(R.id.monthlyStatsLayout);

        btnDailyStats = view.findViewById(R.id.btnDailyStats);
        btnMonthlyStats = view.findViewById(R.id.btnMonthlyStats);
        btnExportCSV = view.findViewById(R.id.btnExportCSV);
    }

    private void setupButtons() {
        btnDailyStats.setOnClickListener(v -> {
            isDailyView = true;
            updateViewVisibility();
            loadTodayStatistics();
        });

        btnMonthlyStats.setOnClickListener(v -> {
            isDailyView = false;
            updateViewVisibility();
            loadCurrentMonthStatistics();
        });

        btnExportCSV.setOnClickListener(v -> showExportFormatDialog());

        updateViewVisibility();
    }

    private void showExportFormatDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Choose Export Format")
                .setMessage("Select the format you want to export your statistics:")
                .setPositiveButton("CSV", (dialog, which) -> exportStatisticsAsCSV())
                .setNegativeButton("PDF", (dialog, which) -> exportStatisticsAsPDF())
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void exportStatisticsAsCSV() {
        try {
            List<Map<String, String>> statisticsData = handlerDB.getAllStatisticsData();

            if (statisticsData.isEmpty()) {
                Toast.makeText(getContext(), "No data to export", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder csvContent = new StringBuilder();
            csvContent.append("Date,Work Minutes,Break Minutes,Sessions\n");

            for (Map<String, String> row : statisticsData) {
                csvContent.append(row.get("date")).append(",")
                        .append(row.get("work_minutes")).append(",")
                        .append(row.get("break_minutes")).append(",")
                        .append(row.get("sessions")).append("\n");
            }

            saveFileWithPicker(csvContent.toString(), "csv", "text/csv");

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Unexpected error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void exportStatisticsAsPDF() {
        try {
            List<Map<String, String>> statisticsData = handlerDB.getAllStatisticsData();

            if (statisticsData.isEmpty()) {
                Toast.makeText(getContext(), "No data to export", Toast.LENGTH_SHORT).show();
                return;
            }

            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Pomodoro Timer Statistics")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20));

            document.add(new Paragraph("Generated on: " +
                    new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(new Date()))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12));

            document.add(new Paragraph("\n"));

            Table table = new Table(4);
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Date")).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Work Minutes")).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Break Minutes")).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Sessions")).setTextAlignment(TextAlignment.CENTER));

            for (Map<String, String> row : statisticsData) {
                table.addCell(new Cell().add(new Paragraph(row.get("date"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(row.get("work_minutes"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(row.get("break_minutes"))).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(row.get("sessions"))).setTextAlignment(TextAlignment.CENTER));
            }

            document.add(table);

            int totalWorkMinutes = 0, totalBreakMinutes = 0, totalSessions = 0;
            for (Map<String, String> row : statisticsData) {
                totalWorkMinutes += Integer.parseInt(row.get("work_minutes"));
                totalBreakMinutes += Integer.parseInt(row.get("break_minutes"));
                totalSessions += Integer.parseInt(row.get("sessions"));
            }

            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Summary:")
                    .setFontSize(16));
            document.add(new Paragraph("Total Work Time: " + totalWorkMinutes + " minutes (" +
                    String.format("%.1f", totalWorkMinutes / 60.0) + " hours)"));
            document.add(new Paragraph("Total Break Time: " + totalBreakMinutes + " minutes (" +
                    String.format("%.1f", totalBreakMinutes / 60.0) + " hours)"));
            document.add(new Paragraph("Total Sessions: " + totalSessions));

            document.close();

            byte[] pdfBytes = baos.toByteArray();
            savePDFWithPicker(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error creating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void savePDFWithPicker(byte[] pdfBytes) {
        try {
            String fileName = "pomodoro_statistics_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(new Date()) + ".pdf";

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_TITLE, fileName);

            this.pdfBytesToSave = pdfBytes;
            this.fileExtension = "pdf";

            startActivityForResult(intent, 1002);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error opening file picker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFileWithPicker(String content, String extension, String mimeType) {
        try {
            String fileName = "pomodoro_statistics_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(new Date()) + "." + extension;

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(mimeType);
            intent.putExtra(Intent.EXTRA_TITLE, fileName);

            this.contentToSave = content;
            this.fileExtension = extension;

            startActivityForResult(intent, 1001);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error opening file picker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {

                if (requestCode == 1001 && contentToSave != null) {
                    try {
                        java.io.OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri);
                        if (outputStream != null) {
                            outputStream.write(contentToSave.getBytes());
                            outputStream.close();
                            Toast.makeText(getContext(), "CSV file saved successfully!", Toast.LENGTH_LONG).show();

                            offerToOpenFile(uri, "text/csv");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error saving CSV file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    contentToSave = null; // Clear the content

                } else if (requestCode == 1002 && pdfBytesToSave != null) {
                    try {
                        java.io.OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri);
                        if (outputStream != null) {
                            outputStream.write(pdfBytesToSave);
                            outputStream.close();
                            Toast.makeText(getContext(), "PDF file saved successfully!", Toast.LENGTH_LONG).show();
                            offerToOpenFile(uri, "application/pdf");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error saving PDF file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    pdfBytesToSave = null; // Clear the bytes
                }

                fileExtension = null;
            }
        }
    }

    private void offerToOpenFile(Uri fileUri, String mimeType) {
        try {
            Intent openIntent = new Intent(Intent.ACTION_VIEW);
            openIntent.setDataAndType(fileUri, mimeType);
            openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (openIntent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(Intent.createChooser(openIntent, "Open file with..."));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateViewVisibility() {
        if (isDailyView) {
            dailyStatsLayout.setVisibility(View.VISIBLE);
            monthlyStatsLayout.setVisibility(View.GONE);

            btnDailyStats.setBackgroundTintList(getResources().getColorStateList(R.color.firstColor));
            btnMonthlyStats.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
        } else {
            dailyStatsLayout.setVisibility(View.GONE);
            monthlyStatsLayout.setVisibility(View.VISIBLE);

            btnDailyStats.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
            btnMonthlyStats.setBackgroundTintList(getResources().getColorStateList(R.color.firstColor));
        }
    }

    private void setupCalendar() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                Date selectedDate = calendar.getTime();

                if (isDailyView) {
                    loadStatisticsForDate(selectedDate);
                    updateSelectedDateText(selectedDate);
                } else {
                    loadMonthlyStatistics(year, month);
                    updateSelectedMonthText(year, month);
                }
            }
        });
    }

    private void loadTodayStatistics() {
        Date today = new Date();
        loadStatisticsForDate(today);
        updateSelectedDateText(today);
    }

    private void loadCurrentMonthStatistics() {
        Calendar calendar = Calendar.getInstance();
        loadMonthlyStatistics(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        updateSelectedMonthText(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
    }

    private void loadStatisticsForDate(Date date) {
        try {
            int workMinutes = handlerDB.getTotalWorkMinutesForDate(date);
            int breakMinutes = handlerDB.getTotalBreakMinutesForDate(date);
            int sessionCount = handlerDB.getSessionCountForDate(date);

            tvTotalMinutes.setText(String.valueOf(workMinutes));
            tvSessionCount.setText(String.valueOf(sessionCount));

            tvBreakTime.setText(String.valueOf(breakMinutes));
        } catch (Exception e) {
            e.printStackTrace();
            tvTotalMinutes.setText("0");
            tvSessionCount.setText("0");
            tvBreakTime.setText("0");
        }
    }

    private void loadMonthlyStatistics(int year, int month) {
        try {
            Map<String, Integer> workStats = handlerDB.getMonthlyWorkStatistics(year, month);
            int monthlyBreakMinutes = handlerDB.getMonthlyBreakMinutes(year, month);

            tvMonthlyWork.setText(String.valueOf(workStats.get("workTime")));
            tvMonthlyBreak.setText(String.valueOf(monthlyBreakMinutes));
            tvMonthlySessions.setText(String.valueOf(workStats.get("sessions")));
        } catch (Exception e) {
            e.printStackTrace();
            tvMonthlyWork.setText("0");
            tvMonthlyBreak.setText("0");
            tvMonthlySessions.setText("0");
        }
    }

    private void updateSelectedDateText(Date date) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        tvSelectedDate.setText("Statistics for: " + sdf.format(date));
    }

    private void updateSelectedMonthText(int year, int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        tvSelectedDate.setText("Statistics for: " + monthNames[month] + " " + year);
    }
}
