
package com.termproject.ac;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AddNewSection extends AppCompatActivity {

    private EditText new_section_name;
    private Button save, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_section);

        // Initialize EditText and Buttons
        new_section_name = findViewById(R.id.new_section_name);
        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.save);

        // Cancel button simply finishes the activity
        cancel.setOnClickListener(v -> finish());

        // Save button - pass the new section name back to AttendanceCheckActivity
        save.setOnClickListener(v -> {
            // Get the section name from the EditText
            String newSectionName = new_section_name.getText().toString().trim();

            // Check if the section name is empty
            if (!newSectionName.isEmpty()) {
                // Create an intent and pass the new section name
                Intent intent = new Intent();
                intent.putExtra("newSectionName", newSectionName);

                // Set the result and finish the activity
                setResult(RESULT_OK, intent);
                finish();
            } else {
                // Show an alert dialog if the section name is empty
                new AlertDialog.Builder(AddNewSection.this)
                        .setTitle("Invalid Input")
                        .setMessage("Section name cannot be empty.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            }
        });
    }
}
