package com.example.vehicleparkingregistration;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // UI Components
    private Spinner vehicleTypeSpinner;
    private EditText vehicleNumberEditText, rcNumberEditText;
    private Button submitButton, editButton, confirmButton;
    private LinearLayout registrationFormLayout, detailsConfirmationLayout;
    private TextView vehicleTypeTextView, vehicleNumberTextView, rcNumberTextView;

    // Vehicle types array
    private String[] vehicleTypes = {"Car", "Bike", "Truck", "Bus", "Van"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        initializeViews();
        setupSpinner();
        setupListeners();
    }

    private void initializeViews() {
        // Registration form views
        vehicleTypeSpinner = findViewById(R.id.vehicleTypeSpinner);
        vehicleNumberEditText = findViewById(R.id.vehicleNumberEditText);
        rcNumberEditText = findViewById(R.id.rcNumberEditText);
        submitButton = findViewById(R.id.submitButton);
        registrationFormLayout = findViewById(R.id.registrationFormLayout);

        // Confirmation screen views
        detailsConfirmationLayout = findViewById(R.id.detailsConfirmationLayout);
        vehicleTypeTextView = findViewById(R.id.vehicleTypeTextView);
        vehicleNumberTextView = findViewById(R.id.vehicleNumberTextView);
        rcNumberTextView = findViewById(R.id.rcNumberTextView);
        editButton = findViewById(R.id.editButton);
        confirmButton = findViewById(R.id.confirmButton);
    }

    private void setupSpinner() {
        // Create an ArrayAdapter for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                vehicleTypes
        );

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        vehicleTypeSpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        // Submit button click listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate inputs
                if (validateInputs()) {
                    // Show confirmation screen with details
                    showConfirmationScreen();
                }
            }
        });

        // Edit button click listener
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to registration form
                detailsConfirmationLayout.setVisibility(View.GONE);
                registrationFormLayout.setVisibility(View.VISIBLE);
            }
        });

        // Confirm button click listener
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Generate unique serial number and display toast
                generateSerialNumberAndConfirm();

                // Reset form and return to initial state
                resetForm();
            }
        });
    }

    private boolean validateInputs() {
        String vehicleNumber = vehicleNumberEditText.getText().toString().trim();
        String rcNumber = rcNumberEditText.getText().toString().trim();

        // Check if vehicle number is empty
        if (vehicleNumber.isEmpty()) {
            vehicleNumberEditText.setError("Vehicle number cannot be empty");
            return false;
        }

        // Check if RC number is empty
        if (rcNumber.isEmpty()) {
            rcNumberEditText.setError("RC number cannot be empty");
            return false;
        }

        return true;
    }

    private void showConfirmationScreen() {
        // Get the entered details
        String vehicleType = vehicleTypeSpinner.getSelectedItem().toString();
        String vehicleNumber = vehicleNumberEditText.getText().toString().trim();
        String rcNumber = rcNumberEditText.getText().toString().trim();

        // Set the details to confirmation screen
        vehicleTypeTextView.setText(vehicleType);
        vehicleNumberTextView.setText(vehicleNumber);
        rcNumberTextView.setText(rcNumber);

        // Switch layouts
        registrationFormLayout.setVisibility(View.GONE);
        detailsConfirmationLayout.setVisibility(View.VISIBLE);
    }

    private void generateSerialNumberAndConfirm() {
        // Generate a random 6-digit number as serial number
        Random random = new Random();
        int serialNumber = 100000 + random.nextInt(900000);

        // Show confirmation toast
        String toastMessage = "Parking allotted successfully! Your serial number is: " + serialNumber;
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }

    private void resetForm() {
        // Clear all the input fields
        vehicleTypeSpinner.setSelection(0);
        vehicleNumberEditText.setText("");
        rcNumberEditText.setText("");

        // Switch back to registration form
        detailsConfirmationLayout.setVisibility(View.GONE);
        registrationFormLayout.setVisibility(View.VISIBLE);
    }
}