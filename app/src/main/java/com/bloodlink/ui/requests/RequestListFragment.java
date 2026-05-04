package com.bloodlink.ui.requests;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bloodlink.R;
import com.bloodlink.adapters.RequestAdapter;
import com.bloodlink.databinding.FragmentRequestListBinding;

public class RequestListFragment extends Fragment {

    private FragmentRequestListBinding binding;
    private RequestViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater i, ViewGroup c, Bundle s) {
        binding = FragmentRequestListBinding.inflate(i, c, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RequestViewModel.class);

        RequestAdapter adapter = new RequestAdapter(request -> {
            viewModel.selectRequest(request.id);
            Navigation.findNavController(view)
                      .navigate(R.id.action_requestList_to_requestDetail);
        });
        binding.rvRequests.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRequests.setAdapter(adapter);

        setupBloodTypeChips();

        binding.btnSearchCity.setOnClickListener(v -> {
            String city = binding.etSearchCity.getText().toString().trim();
            if (!city.isEmpty()) viewModel.filterByCity(city);
        });
        binding.btnClearFilter.setOnClickListener(v -> {
            binding.etSearchCity.setText("");
            binding.chipGroupBloodType.clearCheck();
            viewModel.clearFilter();
        });

        viewModel.getRequests().observe(getViewLifecycleOwner(), requests -> {
            adapter.submitList(requests);
            binding.tvEmpty.setVisibility(requests.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void setupBloodTypeChips() {
        binding.chipAPos.setOnClickListener(v  -> viewModel.filterByBloodType("A+"));
        binding.chipANeg.setOnClickListener(v  -> viewModel.filterByBloodType("A-"));
        binding.chipBPos.setOnClickListener(v  -> viewModel.filterByBloodType("B+"));
        binding.chipBNeg.setOnClickListener(v  -> viewModel.filterByBloodType("B-"));
        binding.chipABPos.setOnClickListener(v -> viewModel.filterByBloodType("AB+"));
        binding.chipABNeg.setOnClickListener(v -> viewModel.filterByBloodType("AB-"));
        binding.chipOPos.setOnClickListener(v  -> viewModel.filterByBloodType("O+"));
        binding.chipONeg.setOnClickListener(v  -> viewModel.filterByBloodType("O-"));
    }

    @Override
    public void onDestroyView() { super.onDestroyView(); binding = null; }
}
