package com.example.travelticketbooking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // UI Components
    private Spinner sourceSpinner, destinationSpinner;
    private ToggleButton tripTypeToggle;
    private TextView departureDateTextView, returnDateTextView;
    private Button departureDateButton, returnDateButton, submitButton, resetButton;
    private LinearLayout returnDateLayout;
    private EditText passengersEditText;

    // Data
    private Calendar departureDateCalendar, returnDateCalendar;
    private SimpleDateFormat dateFormat;

    // City lists for spinners
    private String[] cities = {
            "New York", "Los Angeles", "Chicago", "Houston", "Phoenix",
            "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Francisco",
            "Tokyo", "Delhi", "Shanghai", "São Paulo", "Mumbai",
            "Beijing", "Cairo", "Dhaka", "Mexico City", "London", "Paris"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize date format
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

        // Initialize calendars with current date
        departureDateCalendar = Calendar.getInstance();
        returnDateCalendar = Calendar.getInstance();
        returnDateCalendar.add(Calendar.DAY_OF_MONTH, 7); // Default return date is a week later

        // Initialize UI elements
        initializeViews();
        setupSpinners();
        setupDatePickers();
        setupToggleButton();
        setupActionButtons();
    }

    private void initializeViews() {
        sourceSpinner = findViewById(R.id.sourceSpinner);
        destinationSpinner = findViewById(R.id.destinationSpinner);
        tripTypeToggle = findViewById(R.id.tripTypeToggle);
        departureDateTextView = findViewById(R.id.departureDateTextView);
        returnDateTextView = findViewById(R.id.returnDateTextView);
        departureDateButton = findViewById(R.id.departureDateButton);
        returnDateButton = findViewById(R.id.returnDateButton);
        returnDateLayout = findViewById(R.id.returnDateLayout);
        submitButton = findViewById(R.id.submitButton);
        resetButton = findViewById(R.id.resetButton);
        passengersEditText = findViewById(R.id.passengersEditText);

        // Set initial date displays
        updateDateDisplay(departureDateTextView, departureDateCalendar);
        updateDateDisplay(returnDateTextView, returnDateCalendar);
    }

    private void setupSpinners() {
        // Create adapter for spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                cities
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set adapters to spinners
        sourceSpinner.setAdapter(adapter);
        destinationSpinner.setAdapter(adapter);

        // Set default selection (different cities)
        destinationSpinner.setSelection(1);
    }

    private void setupDatePickers() {
        // Setup departure date picker
        departureDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(departureDateCalendar, departureDateTextView, true);
            }
        });

        // Setup return date picker
        returnDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(returnDateCalendar, returnDateTextView, false);
            }
        });
    }

    private void setupToggleButton() {
        tripTypeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Show/hide return date based on trip type
                returnDateLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void setupActionButtons() {
        // Setup reset button
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetForm();
            }
        });

        // Setup submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    showConfirmationDialog();
                }
            }
        });
    }

    private void showDatePicker(final Calendar calendar, final TextView textView, final boolean isDeparture) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateDisplay(textView, calendar);

                        // If departure date is changed and is after return date, update return date
                        if (isDeparture && tripTypeToggle.isChecked() &&
                                departureDateCalendar.after(returnDateCalendar)) {
                            returnDateCalendar.setTime(departureDateCalendar.getTime());
                            returnDateCalendar.add(Calendar.DAY_OF_MONTH, 1);
                            updateDateDisplay(returnDateTextView, returnDateCalendar);
                            Toast.makeText(MainActivity.this,
                                    "Return date has been updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date as today for departure
        if (isDeparture) {
            datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        } else {
            // Set minimum date as departure date for return
            datePickerDialog.getDatePicker().setMinDate(departureDateCalendar.getTimeInMillis());
        }

        datePickerDialog.show();
    }

    private void updateDateDisplay(TextView textView, Calendar calendar) {
        textView.setText(dateFormat.format(calendar.getTime()));
    }

    private boolean validateForm() {
        String source = sourceSpinner.getSelectedItem().toString();
        String destination = destinationSpinner.getSelectedItem().toString();
        String passengersText = passengersEditText.getText().toString().trim();

        // Check if source and destination are the same
        if (source.equals(destination)) {
            Toast.makeText(this, "Source and destination cannot be the same", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if number of passengers is entered
        if (passengersText.isEmpty()) {
            passengersEditText.setError("Please enter number of passengers");
            return false;
        }

        // Check if number of passengers is valid
        int passengers;
        try {
            passengers = Integer.parseInt(passengersText);
            if (passengers <= 0 || passengers > 10) {
                passengersEditText.setError("Number of passengers must be between 1 and 10");
                return false;
            }
        } catch (NumberFormatException e) {
            passengersEditText.setError("Please enter a valid number");
            return false;
        }

        return true;
    }

    private void resetForm() {
        // Reset spinners
        sourceSpinner.setSelection(0);
        destinationSpinner.setSelection(1);

        // Reset toggle button
        tripTypeToggle.setChecked(false);
        returnDateLayout.setVisibility(View.GONE);

        // Reset date pickers to current date
        departureDateCalendar = Calendar.getInstance();
        returnDateCalendar = Calendar.getInstance();
        returnDateCalendar.add(Calendar.DAY_OF_MONTH, 7);
        updateDateDisplay(departureDateTextView, departureDateCalendar);
        updateDateDisplay(returnDateTextView, returnDateCalendar);

        // Reset number of passengers
        passengersEditText.setText("");
    }

    private void showConfirmationDialog() {
        // Create a custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.confirmation_dialog, null);
        builder.setView(dialogView);

        // Find views in dialog layout
        TextView sourceDestTextView = dialogView.findViewById(R.id.sourceDestTextView);
        TextView tripTypeTextView = dialogView.findViewById(R.id.tripTypeTextView);
        TextView datesTextView = dialogView.findViewById(R.id.datesTextView);
        TextView passengersTextView = dialogView.findViewById(R.id.passengersTextView);
        Button okButton = dialogView.findViewById(R.id.okButton);

        // Set text values
        String source = sourceSpinner.getSelectedItem().toString();
        String destination = destinationSpinner.getSelectedItem().toString();
        boolean isRoundTrip = tripTypeToggle.isChecked();
        int passengers = Integer.parseInt(passengersEditText.getText().toString().trim());

        sourceDestTextView.setText(source + " to " + destination);
        tripTypeTextView.setText(isRoundTrip ? "Round Trip" : "One Way");

        String datesText = "Departure: " + dateFormat.format(departureDateCalendar.getTime());
        if (isRoundTrip) {
            datesText += "\nReturn: " + dateFormat.format(returnDateCalendar.getTime());
        }
        datesTextView.setText(datesText);

        passengersTextView.setText(passengers + (passengers > 1 ? " passengers" : " passenger"));

        // Create and show the dialog
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Set OK button click listener
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Booking successful!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                resetForm(); // Reset form after successful booking
            }
        });
    }
}