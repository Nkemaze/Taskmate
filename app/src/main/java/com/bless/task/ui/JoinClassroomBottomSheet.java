package com.bless.task.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bless.task.data.Classroom;
import com.bless.task.databinding.LayoutJoinClassroomBinding;
import com.bless.task.viewmodel.ClassroomViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class JoinClassroomBottomSheet extends BottomSheetDialogFragment {

    private LayoutJoinClassroomBinding binding;
    private ClassroomViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutJoinClassroomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ClassroomViewModel.class);

        binding.buttonJoin.setOnClickListener(v -> {
            String code = binding.editClassroomCode.getText().toString().trim().toUpperCase();
            if (TextUtils.isEmpty(code) || code.length() < 6) {
                binding.layoutClassroomCode.setError("Enter a valid 6-digit code");
            } else {
                // In a real app, you'd verify the code with Firebase
                // Here we'll just mock adding a classroom for demonstration
                Classroom mockClassroom = new Classroom("Mobile App Dev", "Room 402", code, "Active", "Learning Android development with Java.");
                viewModel.insert(mockClassroom);
                Toast.makeText(getContext(), "Joined successfully!", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
