package com.termproject.ac;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddNewEvent extends AppCompatActivity {

    private EditText new_event_name; // Input field for new event name
    private Button save, cancel; // Buttons for save and cancel actions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_event);

        // Initialize EditText and Buttons
        new_event_name = findViewById(R.id.new_event_name);
        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.save);

        // Cancel button simply finishes the activity
        cancel.setOnClickListener(v -> finish());

        // Save button - pass the new event name back to AttendanceCheckActivity
        save.setOnClickListener(v -> {
            // Get the event name from the EditText
            String newEventName = new_event_name.getText().toString().trim();

            // Create an intent and pass the new event name
            Intent intent = new Intent();
            intent.putExtra("newEventName", newEventName);

            // Set the result and finish the activity
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}
