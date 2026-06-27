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
            if (oldItem.getId() == null || newItem.getId() == null) return false;
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CampusAlert oldItem, @NonNull CampusAlert newItem) {
            return (oldItem.getTitle() != null && oldItem.getTitle().equals(newItem.getTitle())) &&
                    (oldItem.getMessage() != null && oldItem.getMessage().equals(newItem.getMessage()));
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
        CampusAlert alert = getItem(position);
        if (alert != null) {
            holder.bind(alert);
        }
    }

    static class AlertViewHolder extends RecyclerView.ViewHolder {
        private final ItemAlertBinding binding;

        public AlertViewHolder(ItemAlertBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CampusAlert alert) {
            if (alert == null) return;
            binding.textAlertTitle.setText(alert.getTitle() != null ? alert.getTitle() : "Alert");
            binding.textAlertMessage.setText(alert.getMessage() != null ? alert.getMessage() : "");
            binding.textTimestamp.setText(alert.getTimestamp() != null ? alert.getTimestamp() : "");
        }
    }
}
