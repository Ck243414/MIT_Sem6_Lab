package com.example.movieticketbooking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // UI Components
    private Spinner movieSpinner, theatreSpinner;
    private TextView dateTextView, timeTextView, premiumInfoTextView;
    private Button dateButton, timeButton, resetButton, bookButton;
    private ToggleButton ticketTypeToggle;
    private EditText ticketsEditText;

    // Data
    private Calendar selectedDate, selectedTime;
    private SimpleDateFormat dateFormat, timeFormat;
    private boolean isPremiumSelected = false;

    // Movie and Theatre lists for spinners
    private String[] movies = {
            "Avengers: Endgame", "The Shawshank Redemption", "Inception",
            "The Dark Knight", "Pulp Fiction", "The Godfather",
            "Fight Club", "Interstellar", "The Matrix", "Parasite"
    };

    private String[] theatres = {
            "AMC Theatres", "Regal Cinemas", "Cinemark",
            "Cineplex", "IMAX", "Alamo Drafthouse",
            "Landmark Theatres", "Marcus Theatres", "Galaxy Theatres", "Showcase Cinemas"
    };

    // Seats available (for simulation)
    private HashMap<String, Integer> availableSeats = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize date and time formats
        dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US);
        timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);

        // Initialize calendars with current date and time
        selectedDate = Calendar.getInstance();
        selectedTime = Calendar.getInstance();
        selectedTime.set(Calendar.HOUR_OF_DAY, 14); // Default to 2 PM
        selectedTime.set(Calendar.MINUTE, 0);

        // Initialize UI elements
        initializeViews();
        setupSpinners();
        setupDatePicker();
        setupTimePicker();
        setupToggleButton();
        setupActionButtons();

        // Initialize random available seats for each movie
        initializeAvailableSeats();
    }

    private void initializeViews() {
        movieSpinner = findViewById(R.id.movieSpinner);
        theatreSpinner = findViewById(R.id.theatreSpinner);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);
        ticketTypeToggle = findViewById(R.id.ticketTypeToggle);
        premiumInfoTextView = findViewById(R.id.premiumInfoTextView);
        ticketsEditText = findViewById(R.id.ticketsEditText);
        resetButton = findViewById(R.id.resetButton);
        bookButton = findViewById(R.id.bookButton);

        // Set initial date and time displays
        updateDateDisplay();
        updateTimeDisplay();
    }

    private void setupSpinners() {
        // Create adapter for movie spinner
        ArrayAdapter<String> movieAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                movies
        );
        movieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        movieSpinner.setAdapter(movieAdapter);

        // Create adapter for theatre spinner
        ArrayAdapter<String> theatreAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                theatres
        );
        theatreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        theatreSpinner.setAdapter(theatreAdapter);
    }

    private void setupDatePicker() {
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                selectedDate.set(Calendar.YEAR, year);
                                selectedDate.set(Calendar.MONTH, month);
                                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                updateDateDisplay();
                                validatePremiumTicket();
                            }
                        },
                        selectedDate.get(Calendar.YEAR),
                        selectedDate.get(Calendar.MONTH),
                        selectedDate.get(Calendar.DAY_OF_MONTH)
                );

                // Set minimum date as today
                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());

                // Set maximum date as 2 weeks from today
                Calendar maxDate = Calendar.getInstance();
                maxDate.add(Calendar.DAY_OF_MONTH, 14);
                datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

                datePickerDialog.show();
            }
        });
    }

    private void setupTimePicker() {
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hourOfDay = selectedTime.get(Calendar.HOUR_OF_DAY);
                int minute = selectedTime.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedTime.set(Calendar.MINUTE, minute);
                                updateTimeDisplay();
                                validatePremiumTicket();
                            }
                        },
                        hourOfDay,
                        minute,
                        false  // 12-hour format
                );

                timePickerDialog.show();
            }
        });
    }

    private void setupToggleButton() {
        ticketTypeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isPremiumSelected = isChecked;
                premiumInfoTextView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                validatePremiumTicket();
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

        // Setup book button
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    showBookingConfirmationDialog();
                }
            }
        });
    }

    private void updateDateDisplay() {
        dateTextView.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void updateTimeDisplay() {
        timeTextView.setText(timeFormat.format(selectedTime.getTime()));
    }

    private void validatePremiumTicket() {
        if (isPremiumSelected) {
            int hour = selectedTime.get(Calendar.HOUR_OF_DAY);
            boolean isValidTime = hour >= 12; // Check if time is 12 PM or later

            bookButton.setEnabled(isValidTime);

            if (!isValidTime) {
                Toast.makeText(this,
                        "Premium tickets are only available for shows after 12:00 PM",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            bookButton.setEnabled(true);
        }
    }

    private boolean validateForm() {
        String ticketsText = ticketsEditText.getText().toString().trim();

        // Check if number of tickets is entered
        if (ticketsText.isEmpty()) {
            ticketsEditText.setError("Please enter number of tickets");
            return false;
        }

        // Check if number of tickets is valid
        int numTickets;
        try {
            numTickets = Integer.parseInt(ticketsText);
            if (numTickets <= 0 || numTickets > 10) {
                ticketsEditText.setError("Number of tickets must be between 1 and 10");
                return false;
            }
        } catch (NumberFormatException e) {
            ticketsEditText.setError("Please enter a valid number");
            return false;
        }

        // For premium tickets, check if time is after 12 PM
        if (isPremiumSelected) {
            int hour = selectedTime.get(Calendar.HOUR_OF_DAY);
            if (hour < 12) {
                Toast.makeText(this,
                        "Premium tickets are only available for shows after 12:00 PM",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private void resetForm() {
        // Reset spinners
        movieSpinner.setSelection(0);
        theatreSpinner.setSelection(0);

        // Reset toggle button
        ticketTypeToggle.setChecked(false);
        isPremiumSelected = false;
        premiumInfoTextView.setVisibility(View.GONE);

        // Reset date and time pickers to current date and 2 PM
        selectedDate = Calendar.getInstance();
        selectedTime = Calendar.getInstance();
        selectedTime.set(Calendar.HOUR_OF_DAY, 14);
        selectedTime.set(Calendar.MINUTE, 0);
        updateDateDisplay();
        updateTimeDisplay();

        // Reset number of tickets
        ticketsEditText.setText("");

        // Enable the book button
        bookButton.setEnabled(true);
    }

    private void initializeAvailableSeats() {
        Random random = new Random();
        for (String movie : movies) {
            // Random number of available seats between 10 and 100
            availableSeats.put(movie, random.nextInt(91) + 10);
        }
    }

    private void showBookingConfirmationDialog() {
        // Create a custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.booking_confirmation_dialog, null);
        builder.setView(dialogView);

        // Find views in dialog layout
        TextView movieTextView = dialogView.findViewById(R.id.movieTextView);
        TextView theatreTextView = dialogView.findViewById(R.id.theatreTextView);
        TextView dateTimeTextView = dialogView.findViewById(R.id.dateTimeTextView);
        TextView ticketTypeTextView = dialogView.findViewById(R.id.ticketTypeTextView);
        TextView ticketsTextView = dialogView.findViewById(R.id.ticketsTextView);
        TextView availableSeatsTextView = dialogView.findViewById(R.id.availableSeatsTextView);
        TextView totalPriceTextView = dialogView.findViewById(R.id.totalPriceTextView);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Set text values
        String movie = movieSpinner.getSelectedItem().toString();
        String theatre = theatreSpinner.getSelectedItem().toString();
        String date = dateFormat.format(selectedDate.getTime());
        String time = timeFormat.format(selectedTime.getTime());
        String ticketType = isPremiumSelected ? "Premium" : "Standard";
        int numTickets = Integer.parseInt(ticketsEditText.getText().toString().trim());
        int availableSeatsCount = availableSeats.get(movie);

        // Calculate ticket price based on type and time
        int basePrice = isPremiumSelected ? 15 : 10;
        int hour = selectedTime.get(Calendar.HOUR_OF_DAY);

        // Peak hours (5 PM to 11 PM) get a 20% surcharge
        if (hour >= 17 && hour <= 23) {
            basePrice = (int) (basePrice * 1.2);
        }

        // Weekend (Saturday-Sunday) gets a 10% surcharge
        int dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            basePrice = (int) (basePrice * 1.1);
        }

        int totalPrice = basePrice * numTickets;

        // Set dialog text
        movieTextView.setText(movie);
        theatreTextView.setText(theatre);
        dateTimeTextView.setText(date + " at " + time);
        ticketTypeTextView.setText(ticketType);
        ticketsTextView.setText(String.valueOf(numTickets));
        availableSeatsTextView.setText(String.valueOf(availableSeatsCount));
        totalPriceTextView.setText("$" + totalPrice);

        // Create and show the dialog
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Set button click listeners
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Generate confirmation number
                String confirmationNumber = generateConfirmationNumber();

                // Show confirmation message
                Toast.makeText(MainActivity.this,
                        "Booking confirmed! Your confirmation number is: " + confirmationNumber,
                        Toast.LENGTH_LONG).show();

                // Update available seats
                availableSeats.put(movie, availableSeatsCount - numTickets);

                dialog.dismiss();
                resetForm();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private String generateConfirmationNumber() {
        // Generate a random alphanumeric confirmation number
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
}