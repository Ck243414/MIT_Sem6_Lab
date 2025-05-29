package com.example.imagemenuapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    private ImageView mainImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize ImageView
        mainImageView = findViewById(R.id.mainImageView);
    }

    // Create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Handle menu item selections
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_image1) {
            displayImageAndToast(1);
            return true;
        } else if (itemId == R.id.menu_image2) {
            displayImageAndToast(2);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Method to display image and show toast
    private void displayImageAndToast(int imageId) {
        switch (imageId) {
            case 1:
                mainImageView.setImageResource(R.drawable.image1);
                Toast.makeText(this, "Image-1: First sample image", Toast.LENGTH_LONG).show();
                break;
            case 2:
                mainImageView.setImageResource(R.drawable.image2);
                Toast.makeText(this, "Image-2: Second sample image", Toast.LENGTH_LONG).show();
                break;
        }
    }
}