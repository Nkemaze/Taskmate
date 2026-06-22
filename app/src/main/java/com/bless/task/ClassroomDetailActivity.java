package com.bless.task;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bless.task.databinding.ActivityClassroomDetailBinding;

public class ClassroomDetailActivity extends AppCompatActivity {

    private ActivityClassroomDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClassroomDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        displayDetails();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayDetails() {
        String name = getIntent().getStringExtra("CLASSROOM_NAME");
        String location = getIntent().getStringExtra("CLASSROOM_LOCATION");
        String description = getIntent().getStringExtra("CLASSROOM_DESCRIPTION");

        if (name != null) binding.textClassroomName.setText(name);
        if (location != null) binding.textLocation.setText("📍 " + location);
        if (description != null) binding.textDescription.setText(description);
    }
}
