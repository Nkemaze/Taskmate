package com.bless.task.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bless.task.data.CampusAlert;
import com.bless.task.databinding.ItemAlertBinding;

public class AlertAdapter extends ListAdapter<CampusAlert, AlertAdapter.AlertViewHolder> {

    public AlertAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<CampusAlert> DIFF_CALLBACK = new DiffUtil.ItemCallback<CampusAlert>() {
        @Override
        public boolean areItemsTheSame(@NonNull CampusAlert oldItem, @NonNull CampusAlert newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CampusAlert oldItem, @NonNull CampusAlert newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getMessage().equals(newItem.getMessage()) &&
                    oldItem.getTimestamp().equals(newItem.getTimestamp());
        }
    };

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAlertBinding binding = ItemAlertBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AlertViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class AlertViewHolder extends RecyclerView.ViewHolder {
        private final ItemAlertBinding binding;

        public AlertViewHolder(ItemAlertBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CampusAlert alert) {
            binding.textAlertTitle.setText(alert.getTitle());
            binding.textAlertMessage.setText(alert.getMessage());
            binding.textTimestamp.setText(alert.getTimestamp());
        }
    }
}
