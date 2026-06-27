package com.bless.task;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bless.task.data.Task;
import com.bless.task.databinding.ActivityAddTaskBinding;
import com.bless.task.viewmodel.TaskViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Activity to add or edit a task with multi-reminder support.
 */
public class AddTaskActivity extends AppCompatActivity {

    private ActivityAddTaskBinding binding;
    private TaskViewModel viewModel;
    private final Calendar calendar = Calendar.getInstance();
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
    private final SimpleDateFormat dateTimeFormatDisplay = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault());

    private int taskId = -1;
    private boolean isEditMode = false;
    private final List<Calendar> customReminders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        setupToolbar();
        setupPickers();
        setupReminderLogic();
        setupButtons();
        checkEditMode();
    }

    private void checkEditMode() {
        if (getIntent().hasExtra("TASK_ID")) {
            isEditMode = true;
            taskId = getIntent().getIntExtra("TASK_ID", -1);
            binding.editTaskTitle.setText(getIntent().getStringExtra("TASK_TITLE"));
            binding.editSubject.setText(getIntent().getStringExtra("TASK_SUBJECT"));
            binding.editDueDate.setText(getIntent().getStringExtra("TASK_DUE_DATE"));
            
            if (getIntent().hasExtra("TASK_DUE_TIME")) {
                binding.editDueTime.setText(getIntent().getStringExtra("TASK_DUE_TIME"));
            }
            if (getIntent().hasExtra("TASK_NOTES")) {
                binding.editNotes.setText(getIntent().getStringExtra("TASK_NOTES"));
            }

            // Load Default Reminders
            String defaults = getIntent().getStringExtra("TASK_DEFAULT_REMINDERS");
            if (defaults != null) {
                if (defaults.contains("0m")) binding.chip0m.setChecked(true);
                if (defaults.contains("30m")) binding.chip30m.setChecked(true);
                if (defaults.contains("1h")) binding.chip1h.setChecked(true);
                if (defaults.contains("1d")) binding.chip1d.setChecked(true);
                if (defaults.contains("2d")) binding.chip2d.setChecked(true);
            }

            // Load Custom Reminders
            String customs = getIntent().getStringExtra("TASK_CUSTOM_REMINDERS");
            if (customs != null && !customs.isEmpty()) {
                String[] parts = customs.split(",");
                for (String part : parts) {
                    try {
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(Long.parseLong(part.trim()));
                        addCustomReminderUI(cal);
                    } catch (NumberFormatException ignored) {}
                }
            }
            
            binding.buttonSaveTask.setText("Update Task");
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isEditMode ? "Edit Task" : "Create New Task");
        }
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void setupPickers() {
        binding.editDueDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, day) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                binding.editDueDate.setText(dateFormat.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        binding.editDueTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                binding.editDueTime.setText(timeFormat.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        });
    }

    private void setupReminderLogic() {
        binding.switchReminder.setOnCheckedChangeListener((btn, isChecked) -> {
            binding.layoutReminderOptions.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        binding.btnAddCustomReminder.setOnClickListener(v -> showCustomReminderPicker());
        
        binding.switchReminder.setChecked(true);
    }

    private void showCustomReminderPicker() {
        Calendar now = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(Calendar.YEAR, year);
            selected.set(Calendar.MONTH, month);
            selected.set(Calendar.DAY_OF_MONTH, day);

            new TimePickerDialog(this, (tView, hour, minute) -> {
                selected.set(Calendar.HOUR_OF_DAY, hour);
                selected.set(Calendar.MINUTE, minute);
                selected.set(Calendar.SECOND, 0);
                selected.set(Calendar.MILLISECOND, 0);
                
                if (selected.getTimeInMillis() <= System.currentTimeMillis()) {
                    Toast.makeText(this, "Reminder must be in the future", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                addCustomReminderUI(selected);
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false).show();

        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void addCustomReminderUI(Calendar cal) {
        // Prevent duplicates by timestamp
        for (Calendar c : customReminders) {
            if (c.getTimeInMillis() == cal.getTimeInMillis()) return;
        }
        
        customReminders.add(cal);
        
        View reminderView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, binding.containerCustomReminders, false);
        TextView tv = reminderView.findViewById(android.R.id.text1);
        tv.setText("Alert at: " + dateTimeFormatDisplay.format(cal.getTime()));
        tv.setTextSize(14);
        tv.setPadding(0, 8, 0, 8);
        
        // Remove item on click
        reminderView.setOnClickListener(v -> {
            removeCustomReminderByTime(cal.getTimeInMillis());
            binding.containerCustomReminders.removeView(reminderView);
            Toast.makeText(this, "Reminder removed", Toast.LENGTH_SHORT).show();
        });
        
        binding.containerCustomReminders.addView(reminderView);
    }

    private void removeCustomReminderByTime(long timeInMillis) {
        Iterator<Calendar> iterator = customReminders.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getTimeInMillis() == timeInMillis) {
                iterator.remove();
                break;
            }
        }
    }

    private void setupButtons() {
        binding.buttonSaveTask.setOnClickListener(v -> saveTask());
        binding.buttonDiscard.setOnClickListener(v -> finish());
    }

    private void saveTask() {
        String title = binding.editTaskTitle.getText().toString().trim();
        String subject = binding.editSubject.getText().toString().trim();
        String dueDate = binding.editDueDate.getText().toString().trim();
        String dueTime = binding.editDueTime.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            binding.layoutTaskTitle.setError("Title required");
            return;
        }
        
        if (dueDate.equals("Select Date")) {
            Toast.makeText(this, "Please select a due date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Collect Default Reminders
        List<String> selectedDefaults = new ArrayList<>();
        if (binding.chip0m.isChecked()) selectedDefaults.add("0m");
        if (binding.chip30m.isChecked()) selectedDefaults.add("30m");
        if (binding.chip1h.isChecked()) selectedDefaults.add("1h");
        if (binding.chip1d.isChecked()) selectedDefaults.add("1d");
        if (binding.chip2d.isChecked()) selectedDefaults.add("2d");
        String defaultRemindersStr = TextUtils.join(",", selectedDefaults);

        // Collect Custom Reminders
        List<Long> timestamps = new ArrayList<>();
        for (Calendar c : customReminders) timestamps.add(c.getTimeInMillis());
        String customRemindersStr = TextUtils.join(",", timestamps);

        Task task = new Task(
                title, subject, dueDate, dueTime,
                isEditMode ? getIntent().getIntExtra("TASK_COMPLETED", 0) : 0,
                binding.switchReminder.isChecked() ? 1 : 0,
                defaultRemindersStr,
                customRemindersStr,
                binding.editNotes.getText().toString().trim()
        );

        if (isEditMode) {
            task.setId(taskId);
            viewModel.update(task);
            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.insert(task);
            Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
