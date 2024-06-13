package com.mimeng.ui.develop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mimeng.databinding.FragmentDevelopBinding;

public class DevelopFragment extends Fragment {

    private FragmentDevelopBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DevelopViewModel developViewModel =
                new ViewModelProvider(this).get(DevelopViewModel.class);

        binding = FragmentDevelopBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDevelop;
        developViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}