package com.example.xyzfitnesscenter;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private LinearLayout optionMenuContainer;
    private TextView contentTextView;
    private TextView menuText;
    private FrameLayout contentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        optionMenuContainer = findViewById(R.id.optionMenuContainer);
        contentTextView = findViewById(R.id.contentTextView);
        menuText = findViewById(R.id.menuText);
        contentContainer = findViewById(R.id.contentContainer);

        // Menu button click listener
        menuText.setOnClickListener(v -> {
            if (optionMenuContainer.getVisibility() == View.VISIBLE) {
                optionMenuContainer.setVisibility(View.GONE);
            } else {
                optionMenuContainer.setVisibility(View.VISIBLE);
            }
        });

        // Menu option click listeners
        TextView workoutPlansOption = findViewById(R.id.workoutPlansOption);
        TextView trainersOption = findViewById(R.id.trainersOption);
        TextView membershipOption = findViewById(R.id.membershipOption);

        workoutPlansOption.setOnClickListener(v -> displayWorkoutPlans());
        trainersOption.setOnClickListener(v -> displayTrainers());
        membershipOption.setOnClickListener(v -> displayMembership());

        // Bottom icons click listeners
        ImageView homeIcon = findViewById(R.id.homeIcon);
        ImageView aboutIcon = findViewById(R.id.aboutIcon);
        ImageView contactIcon = findViewById(R.id.contactIcon);

        homeIcon.setOnClickListener(v -> displayHomepage());
        aboutIcon.setOnClickListener(v -> displayAboutUs());
        contactIcon.setOnClickListener(v -> displayContactUs());
    }

    private void displayWorkoutPlans() {
        optionMenuContainer.setVisibility(View.GONE);

        StringBuilder content = new StringBuilder();
        content.append("<h2>Workout Plans</h2>");
        content.append("<p><b>Weight Loss Program:</b> A 12-week program designed to help you lose weight through a combination of cardio and strength training.</p>");
        content.append("<p><b>Cardio Fitness:</b> Improve your cardiovascular health with our specialized cardio routines.</p>");
        content.append("<p><b>Muscle Building:</b> Focus on building muscle mass with our progressive resistance training.</p>");
        content.append("<p><b>Flexibility & Balance:</b> Enhance your flexibility and balance with yoga and pilates-inspired workouts.</p>");

        contentTextView.setText(android.text.Html.fromHtml(content.toString()));
    }

    private void displayTrainers() {
        optionMenuContainer.setVisibility(View.GONE);

        StringBuilder content = new StringBuilder();
        content.append("<h2>Our Trainers</h2>");
        content.append("<p><b>John Smith</b> - Specialization: Weight Loss & Nutrition</p>");
        content.append("<p><b>Sarah Johnson</b> - Specialization: Strength Training & Bodybuilding</p>");
        content.append("<p><b>Michael Lee</b> - Specialization: Cardio & High-Intensity Training</p>");
        content.append("<p><b>Emma Davis</b> - Specialization: Yoga & Flexibility</p>");

        contentTextView.setText(android.text.Html.fromHtml(content.toString()));

        // Note: In a real app, you would load actual images for trainers
        // For simplicity, we're just showing text descriptions here
    }

    private void displayMembership() {
        optionMenuContainer.setVisibility(View.GONE);

        StringBuilder content = new StringBuilder();
        content.append("<h2>Membership Packages</h2>");
        content.append("<p><b>Basic Plan:</b> $29.99/month<br>Access to gym equipment and basic amenities</p>");
        content.append("<p><b>Premium Plan:</b> $49.99/month<br>Basic plan + group classes + locker access</p>");
        content.append("<p><b>Gold Plan:</b> $79.99/month<br>Premium plan + personal training sessions (2/month) + nutrition consultation</p>");
        content.append("<p><b>Day Pass:</b> $9.99<br>One-day access to all basic facilities</p>");

        contentTextView.setText(android.text.Html.fromHtml(content.toString()));
    }

    private void displayHomepage() {
        optionMenuContainer.setVisibility(View.GONE);
        contentTextView.setText("Welcome to XYZ Fitness Center! Click on the menu options or icons below to navigate.");
    }

    private void displayAboutUs() {
        optionMenuContainer.setVisibility(View.GONE);

        StringBuilder content = new StringBuilder();
        content.append("<h2>About XYZ Fitness Center</h2>");
        content.append("<p>Founded in 2010, XYZ Fitness Center is dedicated to helping our members achieve their fitness goals in a supportive and motivating environment.</p>");
        content.append("<p>Our state-of-the-art facilities include the latest fitness equipment, spacious workout areas, and dedicated spaces for group classes.</p>");
        content.append("<p>We believe in a holistic approach to fitness that combines physical training with proper nutrition and mental wellness.</p>");

        contentTextView.setText(android.text.Html.fromHtml(content.toString()));
    }

    private void displayContactUs() {
        optionMenuContainer.setVisibility(View.GONE);

        StringBuilder content = new StringBuilder();
        content.append("<h2>Contact Us</h2>");
        content.append("<p><b>Address:</b> 123 Fitness Avenue, Healthytown, XY 12345</p>");
        content.append("<p><b>Phone:</b> (123) 456-7890</p>");
        content.append("<p><b>Email:</b> info@xyzfitness.com</p>");
        content.append("<p><b>Hours:</b><br>Monday-Friday: 5:00 AM - 11:00 PM<br>Saturday-Sunday: 7:00 AM - 9:00 PM</p>");

        contentTextView.setText(android.text.Html.fromHtml(content.toString()));
    }
}