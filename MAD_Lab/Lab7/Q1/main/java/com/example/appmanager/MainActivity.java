package com.example.appmanager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listViewApps;
    private List<ApplicationInfo> appList;
    private PackageManager packageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views and package manager
        listViewApps = findViewById(R.id.listViewApps);
        packageManager = getPackageManager();

        // Load and display installed apps
        loadInstalledApps();

        // Register context menu for long press
        registerForContextMenu(listViewApps);

        // Single tap to launch app
        listViewApps.setOnItemClickListener((parent, view, position, id) -> {
            ApplicationInfo selectedApp = appList.get(position);
            Intent launchIntent = packageManager.getLaunchIntentForPackage(selectedApp.packageName);

            if (launchIntent != null) {
                try {
                    startActivity(launchIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "Unable to launch app", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No launch intent found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadInstalledApps() {
        // Get list of installed apps
        appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        // Filter out apps that don't have launch intent
        List<String> appNames = new ArrayList<>();
        List<ApplicationInfo> filteredAppList = new ArrayList<>();

        for (ApplicationInfo app : appList) {
            Intent launchIntent = packageManager.getLaunchIntentForPackage(app.packageName);
            if (launchIntent != null) {
                appNames.add(packageManager.getApplicationLabel(app).toString());
                filteredAppList.add(app);
            }
        }

        // Update app list with filtered list
        appList = filteredAppList;

        // Create and set adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                appNames
        );
        listViewApps.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.app_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (info == null) return false;

        int position = info.position;
        ApplicationInfo selectedApp = appList.get(position);

        int itemId = item.getItemId();
        if (itemId == R.id.menu_app_type) {
            showAppType(selectedApp);
            return true;
        } else if (itemId == R.id.menu_app_details) {
            showAppDetails(selectedApp);
            return true;
        } else if (itemId == R.id.menu_uninstall) {
            uninstallApp(selectedApp);
            return true;
        }

        return super.onContextItemSelected(item);
    }

    private void showAppType(ApplicationInfo app) {
        boolean isSystemApp = (app.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        String appType = isSystemApp ? "System App" : "User-Installed App";

        new AlertDialog.Builder(this)
                .setTitle("App Type")
                .setMessage(appType)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showAppDetails(ApplicationInfo app) {
        try {
            String details = "Package Name: " + app.packageName + "\n" +
                    "Version: " + packageManager.getPackageInfo(app.packageName, 0).versionName + "\n" +
                    "Installed Size: " + getAppSize(app);

            new AlertDialog.Builder(this)
                    .setTitle("App Details")
                    .setMessage(details)
                    .setPositiveButton("OK", null)
                    .show();
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "Could not retrieve app details", Toast.LENGTH_SHORT).show();
        }
    }

    private String getAppSize(ApplicationInfo app) {
        long size = 0;
        try {
            File file = new File(app.sourceDir);
            size = file.length();
        } catch (Exception e) {
            return "Size unavailable";
        }

        // Convert to MB with one decimal place
        return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }

    private void uninstallApp(ApplicationInfo app) {
        // Check if app is system app
        if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            Toast.makeText(this, "Cannot uninstall system apps", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Uninstall App")
                .setMessage("Are you sure you want to uninstall " +
                        packageManager.getApplicationLabel(app) + "?")
                .setPositiveButton("Uninstall", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                    intent.setData(Uri.parse("package:" + app.packageName));
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                    startActivityForResult(intent, 1);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "App uninstalled successfully", Toast.LENGTH_SHORT).show();
                // Refresh the list of apps
                loadInstalledApps();
            } else {
                Toast.makeText(this, "Uninstall canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
