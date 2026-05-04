package com.bloodlink.ui.requests;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.bloodlink.adapters.DonorResponseAdapter;
import com.bloodlink.data.database.entities.*;
import com.bloodlink.databinding.FragmentRequestDetailBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RequestDetailFragment extends Fragment {

    private FragmentRequestDetailBinding binding;
    private RequestViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater i, ViewGroup c, Bundle s) {
        binding = FragmentRequestDetailBinding.inflate(i, c, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RequestViewModel.class);

        viewModel.getSelectedRequest().observe(getViewLifecycleOwner(), req -> {
            if (req == null) return;
            bindRequest(req);
        });

        viewModel.operationResult.observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT).show();
            }
        });

        viewModel.operationSuccess.observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                viewModel.clearOperationState();
                Navigation.findNavController(view).navigateUp();
            }
        });
    }

    private void bindRequest(BloodRequest req) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", new Locale("bg"));
        boolean isOwner = (req.requesterId == viewModel.getCurrentUserId());

        // Показваме кръвна група с голям акцент
        binding.tvBloodType.setText(req.bloodType);
        binding.tvUrgency.setText(req.getUrgencyDisplay());
        binding.tvHospital.setText("🏥 " + req.hospital);
        binding.tvCity.setText("📍 " + req.city);
        binding.tvUnits.setText("Нужни единици: " + req.unitsNeeded);
        binding.tvStatus.setText(req.getStatusDisplay());
        binding.tvDate.setText("Публикувано: " + sdf.format(new Date(req.createdAt)));
        binding.tvDescription.setText(req.description != null && !req.description.isEmpty()
                ? req.description : "Няма допълнително описание");

        if (req.contactPhone != null && !req.contactPhone.isEmpty()) {
            binding.tvContact.setVisibility(View.VISIBLE);
            binding.tvContact.setText("📞 " + req.contactPhone);
        }
        if (req.patientName != null && !req.patientName.isEmpty()) {
            binding.tvPatient.setVisibility(View.VISIBLE);
            binding.tvPatient.setText("👤 Пациент: " + req.patientName);
        }

        // Цвят спрямо спешност
        int color;
        switch (req.urgencyLevel) {
            case BloodRequest.URGENCY_CRITICAL: color = 0xFFB71C1C; break;
            case BloodRequest.URGENCY_HIGH:     color = 0xFFE65100; break;
            default:                            color = 0xFFE53935;
        }
        binding.cardHeader.setCardBackgroundColor(color);

        if (isOwner && BloodRequest.STATUS_OPEN.equals(req.status)) {
            // Собственикът управлява откликите
            binding.layoutOwnerActions.setVisibility(View.VISIBLE);
            binding.btnRespond.setVisibility(View.GONE);
            setupResponsesRecycler(req.id);

            binding.btnCloseRequest.setOnClickListener(v ->
                    new AlertDialog.Builder(requireContext())
                        .setTitle("Затвори заявката")
                        .setMessage("Сигурни ли сте?")
                        .setPositiveButton("Да", (d, w) -> viewModel.closeRequest(req.id))
                        .setNegativeButton("Не", null).show());
        } else if (!isOwner && BloodRequest.STATUS_OPEN.equals(req.status)) {
            // Донорът вижда бутон за отклик
            binding.layoutOwnerActions.setVisibility(View.GONE);
            binding.btnRespond.setVisibility(View.VISIBLE);
            binding.btnRespond.setOnClickListener(v -> showRespondDialog(req.id));
        } else {
            binding.layoutOwnerActions.setVisibility(View.GONE);
            binding.btnRespond.setVisibility(View.GONE);
        }
    }

    private void setupResponsesRecycler(int requestId) {
        DonorResponseAdapter adapter = new DonorResponseAdapter(
                id -> viewModel.confirmDonor(id),
                id -> viewModel.rejectDonor(id),
                (responseId, reqId) -> viewModel.markAsDonated(responseId, reqId)
        );
        binding.rvResponses.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvResponses.setAdapter(adapter);

        viewModel.getResponsesForSelected().observe(getViewLifecycleOwner(), responses -> {
            adapter.submitList(responses);
            binding.tvNoResponses.setVisibility(responses.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void showRespondDialog(int requestId) {
        EditText etMsg = new EditText(requireContext());
        etMsg.setHint("Съобщение (незадължително): Мога да дойда в 14:00...");
        new AlertDialog.Builder(requireContext())
                .setTitle("🩸 Отклик за даряване")
                .setMessage("Ще изпратим известие на нуждаещия се.")
                .setView(etMsg)
                .setPositiveButton("Откликни", (d, w) ->
                        viewModel.respondToRequest(requestId, etMsg.getText().toString()))
                .setNegativeButton("Отказ", null)
                .show();
    }

    @Override
    public void onDestroyView() { super.onDestroyView(); binding = null; }
}
