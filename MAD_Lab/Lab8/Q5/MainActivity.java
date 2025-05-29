package com.example.sharedpreferencesapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPhone;

    private static final String PREFS_NAME = "UserDataPrefs";
    private static final String KEY_NAME = "userName";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_PHONE = "userPhone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize EditText fields
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);

        // Load saved data
        loadSavedData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save data when the activity is paused (app closed or moved to background)
        saveData();
    }

    private void saveData() {
        // Get SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Create an editor to write data
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save the values from EditText fields
        editor.putString(KEY_NAME, editTextName.getText().toString());
        editor.putString(KEY_EMAIL, editTextEmail.getText().toString());
        editor.putString(KEY_PHONE, editTextPhone.getText().toString());

        // Apply changes
        editor.apply();

        // Optional: Show a toast to confirm saving
        Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show();
    }

    private void loadSavedData() {
        // Get SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Retrieve saved values, with empty string as default
        String savedName = sharedPreferences.getString(KEY_NAME, "");
        String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
        String savedPhone = sharedPreferences.getString(KEY_PHONE, "");

        // Set the retrieved values to EditText fields
        editTextName.setText(savedName);
        editTextEmail.setText(savedEmail);
        editTextPhone.setText(savedPhone);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure data is saved when the activity is destroyed
        saveData();
    }
}
