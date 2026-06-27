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
import com.bless.task.data.LocalAlert;
import com.bless.task.data.TaskDatabase;
import com.bless.task.databinding.FragmentNotificationsBinding;
import com.bless.task.repository.FirebaseService;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private final MutableLiveData<List<CampusAlert>> firebaseAlerts = new MutableLiveData<>();
    private List<CampusAlert> localAlertsMapped = new ArrayList<>();
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
        observeData();
        fetchAlerts();

        binding.buttonRefresh.setOnClickListener(v -> fetchAlerts());
    }

    private void setupRecyclerView() {
        adapter = new AlertAdapter();
        binding.recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewNotifications.setAdapter(adapter);
    }

    private void observeData() {
        // Observe Local Reminders (Task Alerts)
        TaskDatabase.getDatabase(requireContext()).localAlertDao().getAllAlerts().observe(getViewLifecycleOwner(), locals -> {
            localAlertsMapped = new ArrayList<>();
            if (locals != null) {
                for (LocalAlert local : locals) {
                    CampusAlert mapped = new CampusAlert(local.getTitle(), local.getMessage(), local.getTimestamp(), local.getType());
                    mapped.setId("local_" + local.getId());
                    localAlertsMapped.add(mapped);
                }
            }
            updateUI();
        });

        // Observe Cloud Alerts (Firebase)
        firebaseAlerts.observe(getViewLifecycleOwner(), alerts -> {
            updateUI();
        });
    }

    private void updateUI() {
        if (binding == null) return;

        List<CampusAlert> combined = new ArrayList<>(localAlertsMapped);
        List<CampusAlert> cloud = firebaseAlerts.getValue();
        if (cloud != null) {
            combined.addAll(cloud);
        }

        binding.progressBar.setVisibility(View.GONE);
        if (combined.isEmpty()) {
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            binding.recyclerViewNotifications.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyState.setVisibility(View.GONE);
            binding.recyclerViewNotifications.setVisibility(View.VISIBLE);
            adapter.submitList(combined);
        }
    }

    private void fetchAlerts() {
        if (binding != null) {
            binding.progressBar.setVisibility(View.VISIBLE);
            firebaseService.fetchCampusAlerts(requireContext(), firebaseAlerts);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
