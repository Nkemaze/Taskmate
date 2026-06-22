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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bless.task.ClassroomDetailActivity;
import com.bless.task.adapter.ClassroomAdapter;
import com.bless.task.data.Classroom;
import com.bless.task.databinding.FragmentClassroomsBinding;
import com.bless.task.viewmodel.ClassroomViewModel;

public class ClassroomsFragment extends Fragment implements ClassroomAdapter.OnClassroomClickListener {

    private FragmentClassroomsBinding binding;
    private ClassroomViewModel viewModel;
    private ClassroomAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentClassroomsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ClassroomViewModel.class);

        setupRecyclerView();
        setupFAB();
        observeClassrooms();
    }

    private void setupRecyclerView() {
        adapter = new ClassroomAdapter(this);
        binding.recyclerViewClassrooms.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewClassrooms.setAdapter(adapter);
    }

    private void setupFAB() {
        binding.fabJoinClassroom.setOnClickListener(v -> {
            JoinClassroomBottomSheet bottomSheet = new JoinClassroomBottomSheet();
            bottomSheet.show(getChildFragmentManager(), "JoinClassroomBottomSheet");
        });
    }

    private void observeClassrooms() {
        viewModel.getAllClassrooms().observe(getViewLifecycleOwner(), classrooms -> {
            adapter.submitList(classrooms);
            if (classrooms == null || classrooms.isEmpty()) {
                binding.layoutEmptyState.setVisibility(View.VISIBLE);
                binding.recyclerViewClassrooms.setVisibility(View.GONE);
            } else {
                binding.layoutEmptyState.setVisibility(View.GONE);
                binding.recyclerViewClassrooms.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClassroomClick(Classroom classroom) {
        Intent intent = new Intent(getActivity(), ClassroomDetailActivity.class);
        intent.putExtra("CLASSROOM_NAME", classroom.getName());
        intent.putExtra("CLASSROOM_LOCATION", classroom.getLocation());
        intent.putExtra("CLASSROOM_DESCRIPTION", classroom.getDescription());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
