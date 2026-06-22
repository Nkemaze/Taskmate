package com.bless.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bless.task.databinding.ActivityMainBinding;
import com.bless.task.ui.ClassroomsFragment;
import com.bless.task.ui.NotificationsFragment;
import com.bless.task.ui.TasksFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkInternetConnection();

        binding.layoutError.buttonRetry.setOnClickListener(v -> checkInternetConnection());

        // Default fragment
        loadFragment(new TasksFragment(), "Tasks");

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_tasks) {
                loadFragment(new TasksFragment(), "Tasks");
                return true;
            } else if (itemId == R.id.navigation_classrooms) {
                loadFragment(new ClassroomsFragment(), "Classrooms");
                return true;
            } else if (itemId == R.id.navigation_notifications) {
                loadFragment(new NotificationsFragment(), "Notifications");
                return true;
            }
            return false;
        });
    }

    private void checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            binding.layoutError.getRoot().setVisibility(View.GONE);
        } else {
            binding.layoutError.getRoot().setVisibility(View.VISIBLE);
        }
    }

    private void loadFragment(Fragment fragment, String title) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.nav_host_fragment, fragment);
        ft.commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        } else {
            binding.toolbar.setTitle(title);
        }
    }
}
