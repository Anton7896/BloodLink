package com.bloodlink.ui.responses;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.snackbar.Snackbar;
import com.bloodlink.adapters.MyResponseAdapter;
import com.bloodlink.databinding.FragmentMyResponsesBinding;
import com.bloodlink.ui.requests.RequestViewModel;

public class MyResponsesFragment extends Fragment {

    private FragmentMyResponsesBinding binding;
    private RequestViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater i, ViewGroup c, Bundle s) {
        binding = FragmentMyResponsesBinding.inflate(i, c, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RequestViewModel.class);

        MyResponseAdapter adapter = new MyResponseAdapter();
        binding.rvResponses.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvResponses.setAdapter(adapter);

        viewModel.getMyResponses().observe(getViewLifecycleOwner(), responses -> {
            adapter.submitList(responses);
            binding.tvEmpty.setVisibility(responses.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.operationResult.observe(getViewLifecycleOwner(), msg ->
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() { super.onDestroyView(); binding = null; }
}
