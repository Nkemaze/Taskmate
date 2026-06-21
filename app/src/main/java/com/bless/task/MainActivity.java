package com.bless.task;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bless.task.adapter.TaskAdapter;
import com.bless.task.data.Task;
import com.bless.task.databinding.ActivityMainBinding;
import com.bless.task.viewmodel.TaskViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * Main Activity showing the list of tasks with a professional Material 3 UI.
 */
public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private ActivityMainBinding binding;
    private TaskViewModel viewModel;
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        setupRecyclerView();
        setupFAB();
        observeTasks();
        setupSwipeToDelete();
    }

    private void setupRecyclerView() {
        adapter = new TaskAdapter(this);
        binding.recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewTasks.setAdapter(adapter);
        
        // Add some padding to the bottom so the FAB doesn't cover the last item
        binding.recyclerViewTasks.setClipToPadding(false);
        binding.recyclerViewTasks.setPadding(0, 0, 0, 100);
    }

    private void setupFAB() {
        binding.fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });
    }

    private void observeTasks() {
        viewModel.getAllTasks().observe(this, tasks -> {
            adapter.submitList(tasks);
            if (tasks == null || tasks.isEmpty()) {
                binding.layoutEmptyState.setVisibility(View.VISIBLE);
                binding.recyclerViewTasks.setVisibility(View.GONE);
            } else {
                binding.layoutEmptyState.setVisibility(View.GONE);
                binding.recyclerViewTasks.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Task taskToDelete = adapter.getTaskAt(position);
                deleteTask(taskToDelete);
            }
        }).attachToRecyclerView(binding.recyclerViewTasks);
    }

    private void deleteTask(Task task) {
        viewModel.delete(task);
        Snackbar.make(binding.main, "Task removed", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> viewModel.insert(task))
                .setAnchorView(binding.fabAddTask)
                .show();
    }

    @Override
    public void onTaskCheckedChange(Task task, boolean isChecked) {
        task.setIsCompleted(isChecked ? 1 : 0);
        viewModel.update(task);
    }

    @Override
    public void onEditClick(Task task) {
        Intent intent = new Intent(this, AddTaskActivity.class);
        intent.putExtra("TASK_ID", task.getId());
        intent.putExtra("TASK_TITLE", task.getTitle());
        intent.putExtra("TASK_SUBJECT", task.getSubject());
        intent.putExtra("TASK_DUE_DATE", task.getDueDate());
        intent.putExtra("TASK_COMPLETED", task.getIsCompleted());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Task task) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> deleteTask(task))
                .setNegativeButton("Cancel", null)
                .show();
    }
}
