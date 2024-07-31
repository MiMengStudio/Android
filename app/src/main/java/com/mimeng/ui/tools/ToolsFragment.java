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

    private FragmentToolsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ToolsViewModel toolsViewModel = new ViewModelProvider(this).get(ToolsViewModel.class);

        binding = FragmentToolsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textTools;
        toolsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
