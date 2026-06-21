package com.bless.task.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bless.task.R;
import com.bless.task.data.Task;
import com.bless.task.databinding.ItemTaskBinding;

/**
 * Adapter for the Task RecyclerView.
 */
public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    private final OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskCheckedChange(Task task, boolean isChecked);
        void onEditClick(Task task);
        void onDeleteClick(Task task);
    }

    public TaskAdapter(OnTaskClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK = new DiffUtil.ItemCallback<Task>() {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getSubject().equals(newItem.getSubject()) &&
                    oldItem.getDueDate().equals(newItem.getDueDate()) &&
                    oldItem.getIsCompleted() == newItem.getIsCompleted();
        }
    };

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public Task getTaskAt(int position) {
        return getItem(position);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final ItemTaskBinding binding;

        public TaskViewHolder(ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Task task) {
            binding.textTaskTitle.setText(task.getTitle());
            binding.textDueDate.setText(task.getDueDate());
            binding.chipSubject.setText(task.getSubject());
            
            // Remove listener before setting check state to avoid recursion
            binding.checkboxCompleted.setOnCheckedChangeListener(null);
            binding.checkboxCompleted.setChecked(task.getIsCompleted() == 1);

            // Subject color logic
            int color = getSubjectColor(task.getSubject(), itemView.getContext());
            binding.chipSubject.setChipBackgroundColorResource(color);

            // Completion UI logic
            if (task.getIsCompleted() == 1) {
                binding.textTaskTitle.setPaintFlags(binding.textTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.textTaskTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_grey));
                binding.getRoot().setAlpha(0.6f);
            } else {
                binding.textTaskTitle.setPaintFlags(binding.textTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                binding.textTaskTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.on_surface));
                binding.getRoot().setAlpha(1.0f);
            }

            binding.checkboxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onTaskCheckedChange(task, isChecked);
                }
            });

            binding.buttonEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(task);
                }
            });

            binding.buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(task);
                }
            });
        }

        private int getSubjectColor(String subject, Context context) {
            if (subject == null) return R.color.subject_default;
            String lower = subject.toLowerCase();
            if (lower.contains("math")) return R.color.subject_red;
            if (lower.contains("science") || lower.contains("bio") || lower.contains("phys") || lower.contains("chem")) return R.color.subject_green;
            if (lower.contains("hist") || lower.contains("social") || lower.contains("geo")) return R.color.subject_orange;
            if (lower.contains("eng") || lower.contains("lit") || lower.contains("lang")) return R.color.subject_blue;
            if (lower.contains("art") || lower.contains("mus") || lower.contains("design")) return R.color.subject_purple;
            if (lower.contains("comp") || lower.contains("tech") || lower.contains("it")) return R.color.subject_teal;
            return R.color.subject_default;
        }
    }
}
