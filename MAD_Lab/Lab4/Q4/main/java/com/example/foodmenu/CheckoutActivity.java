package com.example.foodmenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CheckoutActivity extends AppCompatActivity {
    private TextView txtBill;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        txtBill = findViewById(R.id.txtBill);
        btnBack = findViewById(R.id.btnBack);

        Intent intent = getIntent();
        String items = intent.getStringExtra("items");
        double total = intent.getDoubleExtra("total", 0);
        double tax = intent.getDoubleExtra("tax", 0);
        double grandTotal = intent.getDoubleExtra("grandTotal", 0);

        String bill = "Selected Items:\n" + items +
                "\nTotal: $" + String.format("%.2f", total) +
                "\nTax (10%): $" + String.format("%.2f", tax) +
                "\nGrand Total: $" + String.format("%.2f", grandTotal);

        txtBill.setText(bill);

        btnBack.setOnClickListener(view -> finish());
    }
}
