package com.example.digitaltransformationviewer;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView contentTextView;
    private String originalContent;
    private List<String> paragraphs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize TextView
        contentTextView = findViewById(R.id.contentTextView);

        // Set original content
        originalContent = getString(R.string.digital_transformation_content);
        paragraphs = new ArrayList<>(Arrays.asList(originalContent.split("\n\n")));

        // Display content
        contentTextView.setText(originalContent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Setup SearchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search keywords");

        // Search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchContent(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_highlight) {
            showHighlightDialog();
            return true;
        } else if (itemId == R.id.action_sort_alphabetical) {
            sortContentAlphabetically();
            return true;
        } else if (itemId == R.id.action_sort_relevance) {
            sortContentByRelevance();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void searchContent(String query) {
        String lowercaseQuery = query.toLowerCase(Locale.ROOT);
        List<String> matchingParagraphs = new ArrayList<>();

        for (String paragraph : paragraphs) {
            if (paragraph.toLowerCase(Locale.ROOT).contains(lowercaseQuery)) {
                matchingParagraphs.add(paragraph);
            }
        }

        if (!matchingParagraphs.isEmpty()) {
            String resultContent = String.join("\n\n", matchingParagraphs);
            contentTextView.setText(resultContent);
            Toast.makeText(this, matchingParagraphs.size() + " matching paragraphs found", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No matching paragraphs found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showHighlightDialog() {
        // Create a dialog for highlight input
        SearchView highlightView = new SearchView(this);
        highlightView.setQueryHint("Enter words to highlight");

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Highlight Words")
                .setView(highlightView)
                .setPositiveButton("Highlight", (dialog, which) -> {
                    String highlightText = highlightView.getQuery().toString();
                    highlightContent(highlightText);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void highlightContent(String highlightText) {
        SpannableString spannableString = new SpannableString(originalContent);

        // Split multiple keywords
        String[] keywords = highlightText.split("\\s+");

        for (String keyword : keywords) {
            int startIndex = 0;
            while ((startIndex = originalContent.toLowerCase().indexOf(keyword.toLowerCase(), startIndex)) != -1) {
                spannableString.setSpan(
                        new BackgroundColorSpan(Color.YELLOW),
                        startIndex,
                        startIndex + keyword.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                startIndex += keyword.length();
            }
        }
        contentTextView.setText(spannableString);
    }

    private void sortContentAlphabetically() {
        Collections.sort(paragraphs);
        String sortedContent = String.join("\n\n", paragraphs);
        contentTextView.setText(sortedContent);
        Toast.makeText(this, "Content sorted alphabetically", Toast.LENGTH_SHORT).show();
    }

    private void sortContentByRelevance() {
        // Simple relevance sorting based on word count
        paragraphs.sort(Comparator.comparingInt((String p) -> p.split("\\s+").length).reversed());
        String sortedContent = String.join("\n\n", paragraphs);
        contentTextView.setText(sortedContent);
        Toast.makeText(this, "Content sorted by relevance", Toast.LENGTH_SHORT).show();
    }
}