package com.mimeng.ui.tools;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mimeng.databinding.FragmentToolsBinding;

public class ToolsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ToolsViewModel toolsViewModel = new ViewModelProvider(this).get(ToolsViewModel.class);

        com.mimeng.databinding.FragmentToolsBinding binding = FragmentToolsBinding.inflate(inflater, container, false);

        final TextView textView = binding.textTools;
        toolsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
