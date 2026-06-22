package com.bless.task.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bless.task.adapter.AlertAdapter;
import com.bless.task.data.CampusAlert;
import com.bless.task.databinding.FragmentNotificationsBinding;
import com.bless.task.repository.FirebaseService;

import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private final MutableLiveData<List<CampusAlert>> alertsLiveData = new MutableLiveData<>();
    private final FirebaseService firebaseService = new FirebaseService();
    private AlertAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        observeAlerts();

        // Fetch initially
        fetchAlerts();

        binding.buttonRefresh.setOnClickListener(v -> fetchAlerts());
    }

    private void setupRecyclerView() {
        adapter = new AlertAdapter();
        binding.recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewNotifications.setAdapter(adapter);
    }

    private void observeAlerts() {
        alertsLiveData.observe(getViewLifecycleOwner(), alerts -> {
            binding.progressBar.setVisibility(View.GONE);
            if (alerts == null || alerts.isEmpty()) {
                binding.layoutEmptyState.setVisibility(View.VISIBLE);
                binding.recyclerViewNotifications.setVisibility(View.GONE);
            } else {
                binding.layoutEmptyState.setVisibility(View.GONE);
                binding.recyclerViewNotifications.setVisibility(View.VISIBLE);
                adapter.submitList(alerts);
            }
        });
    }

    private void fetchAlerts() {
        binding.progressBar.setVisibility(View.VISIBLE);
        firebaseService.fetchCampusAlerts(alertsLiveData);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
