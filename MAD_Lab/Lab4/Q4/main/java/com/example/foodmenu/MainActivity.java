package com.example.foodmenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private RadioButton pizzaBtn, burgerBtn, pastaBtn,sandwichBtn;
    private Button checkoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pizzaBtn = findViewById(R.id.radioPizza);
        burgerBtn = findViewById(R.id.radioBurger);
        pastaBtn = findViewById(R.id.radioPasta);
        sandwichBtn = findViewById(R.id.radioSandwich);
        checkoutBtn = findViewById(R.id.btnCheckout);

        checkoutBtn.setOnClickListener(view -> processCheckout());
    }

    private void processCheckout() {
        StringBuilder selectedItems = new StringBuilder();
        double total = 0;

        if (pizzaBtn.isChecked()) {
            selectedItems.append("Pizza - $10.99\n");
            total += 10.99;
        }
        if (burgerBtn.isChecked()) {
            selectedItems.append("Burger - $5.49\n");
            total += 5.49;
        }
        if (pastaBtn.isChecked()) {
            selectedItems.append("Pasta - $7.99\n");
            total += 7.99;
        }
        if(sandwichBtn.isChecked()){
            selectedItems.append("Sandwich - $10.99");
            total+=10.99;
        }

        if (total == 0) {
            Toast.makeText(this, "Select items before checkout!", Toast.LENGTH_SHORT).show();
            return;
        }

        double tax = total * 0.1;
        double grandTotal = total + tax;

        Intent intent = new Intent(MainActivity.this, CheckoutActivity.class);
        intent.putExtra("items", selectedItems.toString());
        intent.putExtra("total", total);
        intent.putExtra("tax", tax);
        intent.putExtra("grandTotal", grandTotal);
        startActivity(intent);
    }
}
