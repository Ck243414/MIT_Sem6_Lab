package com.example.taskmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTaskName, editTextDueDate, editTextDescription;
    private Button buttonSelectDate, buttonSaveTask, buttonCancel;
    private Spinner spinnerPriority;
    private ListView listViewTasks;

    private DatabaseHelper dbHelper;
    private List<Task> taskList;
    private TaskAdapter taskAdapter;
    private SimpleDateFormat dateFormat;
    private int currentEditingTaskId = -1;

    private String[] priorityLevels = {"High", "Medium", "Low"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize date formatter
        dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        // Initialize UI components
        editTextTaskName = findViewById(R.id.editTextTaskName);
        editTextDueDate = findViewById(R.id.editTextDueDate);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonSelectDate = findViewById(R.id.buttonSelectDate);
        buttonSaveTask = findViewById(R.id.buttonSaveTask);
        buttonCancel = findViewById(R.id.buttonCancel);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        listViewTasks = findViewById(R.id.listViewTasks);

        // Set up priority spinner
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, priorityLevels);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        // Initialize task list and adapter
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter();
        listViewTasks.setAdapter(taskAdapter);

        // Load tasks from database
        loadTasks();

        // Set up date picker
        buttonSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Save task button click listener
        buttonSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
            }
        });

        // Cancel button click listener
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearForm();
                currentEditingTaskId = -1;
                buttonSaveTask.setText("Save Task");
                buttonCancel.setVisibility(View.GONE);
            }
        });

        // Set up list view item click listener for editing
        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editTask(position);
            }
        });

        // Set up list view long click listener for delete
        listViewTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteConfirmDialog(position);
                return true;
            }
        });
    }

    private void showDatePickerDialog() {
        // Get current date as default
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // If there's already a date, use it as default
        if (!editTextDueDate.getText().toString().isEmpty()) {
            try {
                Date selectedDate = dateFormat.parse(editTextDueDate.getText().toString());
                c.setTime(selectedDate);
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            } catch (ParseException e) {
                // Use current date as fallback
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, dayOfMonth);
                        editTextDueDate.setText(dateFormat.format(selected.getTime()));
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveTask() {
        String taskName = editTextTaskName.getText().toString().trim();
        String dueDate = editTextDueDate.getText().toString().trim();
        String priority = spinnerPriority.getSelectedItem().toString();
        String description = editTextDescription.getText().toString().trim();

        if (taskName.isEmpty()) {
            Toast.makeText(this, "Please enter a task name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dueDate.isEmpty()) {
            Toast.makeText(this, "Please select a due date", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, taskName);
        values.put(DatabaseHelper.COLUMN_DUE_DATE, dueDate);
        values.put(DatabaseHelper.COLUMN_PRIORITY, priority);
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, description);

        if (currentEditingTaskId == -1) {
            // Insert new task
            long newRowId = db.insert(DatabaseHelper.TABLE_NAME, null, values);
            if (newRowId != -1) {
                Toast.makeText(this, "Task saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error saving task", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update existing task
            String selection = DatabaseHelper.COLUMN_ID + " = ?";
            String[] selectionArgs = {String.valueOf(currentEditingTaskId)};
            int count = db.update(DatabaseHelper.TABLE_NAME, values, selection, selectionArgs);
            if (count > 0) {
                Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error updating task", Toast.LENGTH_SHORT).show();
            }
            // Reset editing state
            currentEditingTaskId = -1;
            buttonSaveTask.setText("Save Task");
            buttonCancel.setVisibility(View.GONE);
        }

        // Clear form and reload tasks
        clearForm();
        loadTasks();
    }

    private void loadTasks() {
        taskList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NAME,
                null,
                null, null, null, null,
                null
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
            String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DUE_DATE));
            String priority = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRIORITY));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));

            Task task = new Task(id, name, dueDate, priority, description);
            taskList.add(task);
        }

        cursor.close();
        taskAdapter.notifyDataSetChanged();
    }

    private void editTask(int position) {
        Task task = taskList.get(position);
        editTextTaskName.setText(task.getName());
        editTextDueDate.setText(task.getDueDate());
        editTextDescription.setText(task.getDescription());

        // Set spinner to the correct priority
        for (int i = 0; i < priorityLevels.length; i++) {
            if (priorityLevels[i].equals(task.getPriority())) {
                spinnerPriority.setSelection(i);
                break;
            }
        }

        // Set current editing task ID
        currentEditingTaskId = task.getId();
        buttonSaveTask.setText("Update Task");
        buttonCancel.setVisibility(View.VISIBLE);

        // Scroll to top to see the form
        View parent = (View) editTextTaskName.getParent().getParent();
        if (parent instanceof ScrollView) {
            ((ScrollView) parent).smoothScrollTo(0, 0);
        }
    }

    private void deleteTask(int position) {
        Task task = taskList.get(position);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(task.getId())};
        int deletedRows = db.delete(DatabaseHelper.TABLE_NAME, selection, selectionArgs);

        if (deletedRows > 0) {
            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
            loadTasks();
        } else {
            Toast.makeText(this, "Error deleting task", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Task");
        builder.setMessage("Are you sure you want to delete this task?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTask(position);
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void clearForm() {
        editTextTaskName.setText("");
        editTextDueDate.setText("");
        editTextDescription.setText("");
        spinnerPriority.setSelection(0);
    }

    // Task class to hold data
    private static class Task {
        private int id;
        private String name;
        private String dueDate;
        private String priority;
        private String description;

        public Task(int id, String name, String dueDate, String priority, String description) {
            this.id = id;
            this.name = name;
            this.dueDate = dueDate;
            this.priority = priority;
            this.description = description;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDueDate() {
            return dueDate;
        }

        public String getPriority() {
            return priority;
        }

        public String getDescription() {
            return description;
        }
    }

    // Custom adapter for displaying tasks with details
    private class TaskAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return taskList.size();
        }

        @Override
        public Object getItem(int position) {
            return taskList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.task_item, parent, false);
                holder = new ViewHolder();
                holder.textViewTaskName = convertView.findViewById(R.id.textViewTaskName);
                holder.textViewDueDate = convertView.findViewById(R.id.textViewDueDate);
                holder.textViewPriority = convertView.findViewById(R.id.textViewPriority);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Task task = taskList.get(position);
            holder.textViewTaskName.setText(task.getName());
            holder.textViewDueDate.setText("Due: " + task.getDueDate());
            holder.textViewPriority.setText("Priority: " + task.getPriority());

            // Set background color based on priority
            int bgColor;
            switch (task.getPriority()) {
                case "High":
                    bgColor = 0xFFFFCCCC; // Light red
                    break;
                case "Medium":
                    bgColor = 0xFFFFEACC; // Light orange
                    break;
                default:
                    bgColor = 0xFFE3F2FD; // Light blue
                    break;
            }
            convertView.setBackgroundColor(bgColor);

            return convertView;
        }

        private class ViewHolder {
            TextView textViewTaskName;
            TextView textViewDueDate;
            TextView textViewPriority;
        }
    }

    // Database Helper class
    private class DatabaseHelper extends SQLiteOpenHelper {
        public static final String DATABASE_NAME = "tasks.db";
        public static final int DATABASE_VERSION = 1;
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DUE_DATE = "due_date";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_DESCRIPTION = "description";

        private static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT, " +
                        COLUMN_DUE_DATE + " TEXT, " +
                        COLUMN_PRIORITY + " TEXT, " +
                        COLUMN_DESCRIPTION + " TEXT)";

        public DatabaseHelper(MainActivity context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
