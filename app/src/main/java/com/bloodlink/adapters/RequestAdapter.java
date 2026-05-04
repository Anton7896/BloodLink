package com.bloodlink.adapters;

import android.view.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;
import com.bloodlink.data.database.entities.BloodRequest;
import com.bloodlink.databinding.ItemRequestBinding;
import java.text.SimpleDateFormat;
import java.util.*;

public class RequestAdapter extends ListAdapter<BloodRequest, RequestAdapter.VH> {

    public interface OnClick { void onClick(BloodRequest r); }
    private final OnClick listener;

    public RequestAdapter(OnClick l) { super(DIFF); this.listener = l; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(ItemRequestBinding.inflate(LayoutInflater.from(p.getContext()), p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) { h.bind(getItem(pos)); }

    public class VH extends RecyclerView.ViewHolder {
        final ItemRequestBinding b;
        VH(ItemRequestBinding b) { super(b.getRoot()); this.b = b; }

        void bind(BloodRequest r) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM HH:mm", new Locale("bg"));
            b.tvBloodType.setText(r.bloodType);
            b.tvHospital.setText(r.hospital);
            b.tvCity.setText("📍 " + r.city);
            b.tvUrgency.setText(r.getUrgencyDisplay());
            b.tvUnits.setText("x" + r.unitsNeeded + " ед.");
            b.tvDate.setText(sdf.format(new Date(r.createdAt)));


            int bg;
            switch (r.urgencyLevel) {
                case BloodRequest.URGENCY_CRITICAL: bg = 0xFFB71C1C; break;
                case BloodRequest.URGENCY_HIGH:     bg = 0xFFE65100; break;
                default:                            bg = 0xFFE53935;
            }
            b.bloodBadge.setBackgroundColor(bg);


            int urgColor;
            switch (r.urgencyLevel) {
                case BloodRequest.URGENCY_CRITICAL: urgColor = 0xFFB71C1C; break;
                case BloodRequest.URGENCY_HIGH:     urgColor = 0xFFE65100; break;
                default:                            urgColor = 0xFFE53935;
            }
            b.tvUrgency.setTextColor(urgColor);

            b.getRoot().setOnClickListener(v -> listener.onClick(r));
        }
    }

    static final DiffUtil.ItemCallback<BloodRequest> DIFF = new DiffUtil.ItemCallback<BloodRequest>() {
        @Override public boolean areItemsTheSame(@NonNull BloodRequest o, @NonNull BloodRequest n) { return o.id == n.id; }
        @Override public boolean areContentsTheSame(@NonNull BloodRequest o, @NonNull BloodRequest n) {
            return o.status.equals(n.status) && o.urgencyLevel.equals(n.urgencyLevel);
        }
    };
}
