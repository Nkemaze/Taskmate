package com.bless.task;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.bless.task.data.Task;
import com.bless.task.databinding.ActivityAddTaskBinding;
import com.bless.task.viewmodel.TaskViewModel;
import com.bless.task.worker.NotificationWorker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Activity to add or edit a task with a modern Material 3 design.
 */
public class AddTaskActivity extends AppCompatActivity {

    private ActivityAddTaskBinding binding;
    private TaskViewModel viewModel;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    
    private int taskId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        checkEditMode();
        setupToolbar();
        setupDatePicker();
        setupSaveButton();
    }

    private void checkEditMode() {
        if (getIntent().hasExtra("TASK_ID")) {
            isEditMode = true;
            taskId = getIntent().getIntExtra("TASK_ID", -1);
            binding.editTaskTitle.setText(getIntent().getStringExtra("TASK_TITLE"));
            binding.editSubject.setText(getIntent().getStringExtra("TASK_SUBJECT"));
            binding.editDueDate.setText(getIntent().getStringExtra("TASK_DUE_DATE"));
            binding.buttonSaveTask.setText("Update Task");
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isEditMode ? "Edit Task" : "New Task");
        }
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void setupDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            binding.editDueDate.setText(dateFormat.format(calendar.getTime()));
        };

        binding.editDueDate.setOnClickListener(v -> new DatePickerDialog(AddTaskActivity.this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void setupSaveButton() {
        binding.buttonSaveTask.setOnClickListener(v -> saveTask());
    }

    private void saveTask() {
        if (binding.editTaskTitle.getText() == null || binding.editSubject.getText() == null || binding.editDueDate.getText() == null) {
            return;
        }

        String title = binding.editTaskTitle.getText().toString().trim();
        String subject = binding.editSubject.getText().toString().trim();
        String dueDate = binding.editDueDate.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            binding.layoutTaskTitle.setError("Title is required");
            return;
        } else {
            binding.layoutTaskTitle.setError(null);
        }

        if (TextUtils.isEmpty(subject)) {
            binding.layoutSubject.setError("Subject is required");
            return;
        } else {
            binding.layoutSubject.setError(null);
        }

        if (TextUtils.isEmpty(dueDate)) {
            binding.layoutDueDate.setError("Due date is required");
            return;
        } else {
            binding.layoutDueDate.setError(null);
        }

        if (isEditMode) {
            Task task = new Task(title, subject, dueDate, getIntent().getIntExtra("TASK_COMPLETED", 0));
            task.setId(taskId);
            viewModel.update(task);
            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
        } else {
            Task task = new Task(title, subject, dueDate, 0);
            viewModel.insert(task);
            scheduleNotification(task);
            Toast.makeText(this, "Task created", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void scheduleNotification(Task task) {
        try {
            Date date = dateFormat.parse(task.getDueDate());
            if (date != null) {
                Calendar notifyCalendar = Calendar.getInstance();
                notifyCalendar.setTime(date);
                notifyCalendar.add(Calendar.DAY_OF_YEAR, -1);
                notifyCalendar.set(Calendar.HOUR_OF_DAY, 8);
                notifyCalendar.set(Calendar.MINUTE, 0);

                long delay = notifyCalendar.getTimeInMillis() - System.currentTimeMillis();

                if (delay > 0) {
                    Data inputData = new Data.Builder()
                            .putString(NotificationWorker.TASK_TITLE, task.getTitle())
                            .putString(NotificationWorker.TASK_SUBJECT, task.getSubject())
                            .build();

                    OneTimeWorkRequest notificationRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                            .setInputData(inputData)
                            .addTag(task.getTitle())
                            .build();

                    WorkManager.getInstance(this).enqueue(notificationRequest);
                }
            }
        } catch (ParseException ignored) {}
    }
}
