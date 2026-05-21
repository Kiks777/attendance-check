package com.termproject.ac;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.termproject.ac.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class AttendanceCheckActivity extends AppCompatActivity {



    private ImageView back;
    private RecyclerView recyclerView;
    private SectionItemViewAdapter adapter;
    private List<String> sectionNameList;
    private FloatingActionButton add_button;
    private TextView no_section_added_message;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private int eventId;

    /**
     * Initializes the activity when created.
     * Retrieves event ID and name from the intent.
     * Sets up UI components like back button, RecyclerView, and FloatingActionButton.
     * Initializes database helper and gets a writable database.
     * Loads section names from the database.
     * Sets up the RecyclerView adapter with the list of section names.
     * Handles click events for the add button to start a new activity.
     * Checks the status of the section list.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_check);

        // Get event ID and name from the intent
        Intent intent = getIntent();
        eventId = intent.getIntExtra("eventId", -1);
        String eventName = intent.getStringExtra("eventName");

        // Setup back button
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());

        // Initialize no section added message
        no_section_added_message = findViewById(R.id.no_section_added_message);

        // Initialize database helper and get writable database
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list of section names
        sectionNameList = new ArrayList<>();
        loadSectionsFromDatabase();

        // Initialize the adapter and set it to the RecyclerView
        adapter = new SectionItemViewAdapter(sectionNameList, this);
        recyclerView.setAdapter(adapter);

        // Setup FloatingActionButton
        add_button = findViewById(R.id.add_button);
        add_button.setOnClickListener(v -> {
            Intent intent_add = new Intent(AttendanceCheckActivity.this, AddNewSection.class);
            startActivityForResult(intent_add, 1); // Start activity with request code 1
        });


        //Checks the status of the section list.
        checkSectionListStatus();
    }

    /**
    *This method loads sections from the database for a specific event.
    *It retrieves section names associated with the given event ID from the "Sections" table in the database.
    *The retrieved section names are then added to a list.
     **/
    private void loadSectionsFromDatabase() {
        // It queries the "Sections" table in the database.
        Cursor cursor = db.query("Sections", null, "event_id=?", new String[]{String.valueOf(eventId)}, null, null, null);
        // Iterates through each row in the cursor.
        while (cursor.moveToNext()) {
            // Retrieves the section name from the current row.
            String sectionName = cursor.getString(cursor.getColumnIndexOrThrow("section_name"));
            // Adds the section name to the list.
            sectionNameList.add(sectionName);
        }
        // Closes the cursor to release resources.
        cursor.close();
    }

    /**
     * Inserts a new section into the database.
     * @param sectionName -  The name of the section to be inserted.
     * This method creates a new entry in the 'Sections' table of the database,
     * associating it with the event ID provided during initialization.
     **/

    private void insertSectionIntoDatabase(String sectionName) {
        // Create an object to hold data that will go into the database
        ContentValues values = new ContentValues();

        // Put the section name into the values object
        values.put("section_name", sectionName);

        // Put the event ID into the values object
        values.put("event_id", eventId);

        // Insert the values into the "Sections" table in the database
        db.insert("Sections", null, values);
    }

    /**
     * This method is called when an activity that was started for a result returns.
     *
     * @param requestCode This is the integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify whom this result came from.
     * @param resultCode  This is the integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     * This method checks if the result is from the activity with requestCode 1, the resultCode is RESULT_OK,
     * and the data is not null. If these conditions are met, it retrieves the new section name from the intent extras.
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check the request code and result code
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Get the new section name from the intent extras
            String newSectionName = data.getStringExtra("newSectionName");

            // Check if the new section name is null or empty
            if (newSectionName != null && !newSectionName.trim().isEmpty()) {
                // Add the new section name to the sectionNameList
                sectionNameList.add(newSectionName);

                // Insert the new section into the database
                insertSectionIntoDatabase(newSectionName);

                // Notify the adapter of the changes
                adapter.notifyDataSetChanged();
                checkSectionListStatus();
            } else {
                // Display an alert dialog if the section name is empty or null
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Invalid Input")
                        .setMessage("Section name cannot be empty.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Dismiss the dialog when the OK button is clicked
                            dialog.dismiss();
                        });
                // Create and show the alert dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }


    /**
     * This method checks if the sectionNameList is null or empty.
     * If the list is null or empty, it shows a message.
     * If the list is not null and not empty, it hides the message.
     */
    public void checkSectionListStatus() {
        // Check if sectionNameList is either null or empty
        if (sectionNameList == null || sectionNameList.isEmpty()) {
            // Show the message if the list is null or empty
            no_section_added_message.setVisibility(View.VISIBLE);
        } else {
            // Hide the message if the list is not null and not empty
            no_section_added_message.setVisibility(View.GONE);
        }
    }

}
