package com.bless.task.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bless.task.AddTaskActivity;
import com.bless.task.adapter.TaskAdapter;
import com.bless.task.data.Task;
import com.bless.task.databinding.FragmentTasksBinding;
import com.bless.task.viewmodel.TaskViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public class TasksFragment extends Fragment implements TaskAdapter.OnTaskClickListener {

    private FragmentTasksBinding binding;
    private TaskViewModel viewModel;
    private TaskAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        setupRecyclerView();
        setupFAB();
        observeTasks();
        setupSwipeToDelete();
    }

    private void setupRecyclerView() {
        adapter = new TaskAdapter(this);
        binding.recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewTasks.setAdapter(adapter);
    }

    private void setupFAB() {
        binding.fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddTaskActivity.class);
            startActivity(intent);
        });
    }

    private void observeTasks() {
        viewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
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
                deleteAlert(taskToDelete);
            }
        }).attachToRecyclerView(binding.recyclerViewTasks);
    }

    private void deleteAlert(Task task) {
        viewModel.delete(task);
        Snackbar.make(binding.getRoot(), "Alert removed", Snackbar.LENGTH_LONG)
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
        Intent intent = new Intent(getActivity(), AddTaskActivity.class);
        intent.putExtra("TASK_ID", task.getId());
        intent.putExtra("TASK_TITLE", task.getTitle());
        intent.putExtra("TASK_SUBJECT", task.getSubject());
        intent.putExtra("TASK_DUE_DATE", task.getDueDate());
        intent.putExtra("TASK_COMPLETED", task.getIsCompleted());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Task task) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Alert")
                .setMessage("Are you sure you want to delete this alert?")
                .setPositiveButton("Delete", (dialog, which) -> deleteAlert(task))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
