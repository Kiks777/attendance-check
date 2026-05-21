package com.termproject.ac;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.termproject.ac.barcodescanner.BarcodeScannerActivity;
import com.termproject.ac.database.AttendanceDbHelper;
import com.termproject.ac.database.DatabaseHelper;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AttendanceListActivity extends AppCompatActivity {
    private ImageView back, exportButton;
    private RecyclerView recyclerView;
    private ItemTableRowAdapter adapter;
    private Map<String, List<AttendanceData>> sectionDataMap;
    private FloatingActionButton camera_button;
    private static final int REQUEST_BARCODE_SCANNER = 1;
    private String formattedCurrentTime;

    private LocalTime currentTime;
    private DateTimeFormatter timeFormatter;
    private RadioButton set_time_in, set_time_out;
    private RadioGroup activitySelection;
    private AttendanceDbHelper dbHelper;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private DatabaseHelper exportDbHelper;

    private String sectionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_list);

        dbHelper = new AttendanceDbHelper(this);
        exportDbHelper = new DatabaseHelper(this);

        activitySelection = findViewById(R.id.activity_selection);
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sectionName = getIntent().getStringExtra("sectionName");
        initializeDataLists();
        List<AttendanceData> dataList = loadAttendanceData(sectionName);
        sectionDataMap.put(sectionName, dataList);

        adapter = new ItemTableRowAdapter(dataList);
        recyclerView.setAdapter(adapter);

        camera_button = findViewById(R.id.camera_button);
        camera_button.setOnClickListener(v -> {
            Intent intent = new Intent(AttendanceListActivity.this, BarcodeScannerActivity.class);
            startActivityForResult(intent, REQUEST_BARCODE_SCANNER);
        });



        set_time_in = findViewById(R.id.set_time_in);
        set_time_out = findViewById(R.id.set_time_out);

        exportButton = findViewById(R.id.export);
        exportButton.setOnClickListener(v -> {
            // Get the section name associated with this button
            sectionName = getIntent().getStringExtra("sectionName");
            if (checkPermission()) {
                exportDataToExcel(sectionName);
            } else {
                requestPermission();
            }
        });



    }

    private LocalTime getCurrentTime() {
        return LocalTime.now();
    }

    private void initializeDataLists() {
        sectionDataMap = new HashMap<>();
    }

    private List<AttendanceData> loadAttendanceData(String sectionName) {
        List<AttendanceData> dataList = dbHelper.getAttendanceForSection(sectionName);
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        return dataList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_BARCODE_SCANNER && resultCode == RESULT_OK && data != null) {
            String scannedData = data.getStringExtra("scannedData");
            if (scannedData != null) {
                String sectionName = getIntent().getStringExtra("sectionName");
                if (sectionName != null) {
                    List<AttendanceData> dataList = sectionDataMap.get(sectionName);
                    if (dataList != null) {
                        handleScannedData(scannedData, dataList, sectionName);
                    } else {
                        Toast.makeText(this, "Data list is null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("AttendanceListActivity", "Section name is null");
                }
            } else {
                Toast.makeText(this, "Scanned data is null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleScannedData(String scannedData, List<AttendanceData> dataList, String sectionName) {
        boolean isNewEntry = true;
        AttendanceData entryToUpdate = null;

        currentTime = LocalTime.now();
        timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault());
        formattedCurrentTime = currentTime.format(timeFormatter);

        // Find existing entry if it exists
        for (AttendanceData entry : dataList) {
            if (entry.getId_number().equals(scannedData)) {
                isNewEntry = false;
                entryToUpdate = entry;
                break;
            }
        }

        if (!isNewEntry && entryToUpdate != null) {
            if (entryToUpdate.isIn() && entryToUpdate.isOut()) {
                // Both "in" and "out" times already exist
                Toast.makeText(this, scannedData + " already exists", Toast.LENGTH_SHORT).show();
            } else {
                // Update existing entry
                if (set_time_in.isChecked()) {
                    handleTimeIn(entryToUpdate, formattedCurrentTime, sectionName);
                } else if (set_time_out.isChecked()) {
                    handleTimeOut(entryToUpdate, formattedCurrentTime, sectionName);
                }
            }
        } else if (isNewEntry) {
            handleNewEntry(scannedData, formattedCurrentTime, dataList, sectionName);
        }

        // Always update the in-memory data list
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            Log.e("AttendanceListActivity", "Adapter is null");
        }
    }

    private void handleTimeIn(AttendanceData entry, String formattedCurrentTime, String sectionName) {
        if (entry.isOut()) {
            Toast.makeText(this, "Can't In if already out", Toast.LENGTH_SHORT).show();
        } else if (!entry.isIn()) {
            entry.setIn(true);
            entry.setTimeIn(formattedCurrentTime);
            dbHelper.updateAttendance(sectionName, entry.getId_number(), formattedCurrentTime, entry.getTimeOut());
        }
    }

    private void handleTimeOut(AttendanceData entry, String formattedCurrentTime, String sectionName) {
        if (entry.getTimeIn() != null && entry.getTimeIn().equals(formattedCurrentTime)) {
            Toast.makeText(this, "Time Out shouldn't be the same as Time In", Toast.LENGTH_SHORT).show();
        } else if (!entry.isOut()) {
            entry.setOut(true);
            entry.setTimeOut(formattedCurrentTime);
            dbHelper.updateAttendance(sectionName, entry.getId_number(), entry.getTimeIn(), formattedCurrentTime);
        }
    }

    private void handleNewEntry(String scannedData, String formattedCurrentTime, List<AttendanceData> dataList, String sectionName) {
        if (set_time_in.isChecked()) {
            AttendanceData newDataIn = new AttendanceData(scannedData, true, false, formattedCurrentTime, null);
            dataList.add(newDataIn);
            dbHelper.addAttendance(sectionName, scannedData, formattedCurrentTime, null);
        } else if (set_time_out.isChecked()) {
            AttendanceData newDataOut = new AttendanceData(scannedData, false, true, null, formattedCurrentTime);
            dataList.add(newDataOut);
            dbHelper.addAttendance(sectionName, scannedData, null, formattedCurrentTime);
        } else {
            Toast.makeText(this, "Please select activity [In/Out]", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportDataToExcel(sectionName);
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private void exportDataToExcel(String sectionName) {
        // Fetch event and section data
        List<DatabaseHelper.EventSectionData> data = exportDbHelper.getEventSectionData();

        // Find the specific section
        DatabaseHelper.EventSectionData sectionData = null;
        for (DatabaseHelper.EventSectionData item : data) {
            if (item.sectionName.equals(sectionName)) {
                sectionData = item;
                break;
            }
        }

        // If section is found, export its data
        if (sectionData != null) {
            // Create a new sheet for the section
            String sheetName = sectionData.sectionName + "-" + sectionData.eventName;
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(sheetName);

            int rowNum = 0;

            // Write event and section information
            Row eventRow = sheet.createRow(rowNum++);
            eventRow.createCell(0).setCellValue("Event Name: " + sectionData.eventName);

            Row sectionRow = sheet.createRow(rowNum++);
            sectionRow.createCell(0).setCellValue("Section Name: " + sectionData.sectionName);

            // Add headers for attendance data
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("ID Number");
            headerRow.createCell(1).setCellValue("In");
            headerRow.createCell(2).setCellValue("Out");

            // Fetch latest attendance data for the section
            List<AttendanceData> attendanceDataList = dbHelper.getAttendanceForSection(sectionName);

            // Write attendance data
            for (AttendanceData attendanceData : attendanceDataList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(attendanceData.getId_number());
                row.createCell(1).setCellValue(attendanceData.isIn() ? "In\n\n" + attendanceData.getTimeIn() : "");
                row.createCell(2).setCellValue(attendanceData.isOut() ? "Out\n\n" + attendanceData.getTimeOut() : "");
            }

            // Save workbook to file with sectionName + eventName format
            String fileName = sectionData.sectionName + "-" + sectionData.eventName + ".xlsx";
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(dir, fileName);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
                Toast.makeText(this, "Data exported to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error exporting data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Section not found", Toast.LENGTH_SHORT).show();
        }
    }




}
