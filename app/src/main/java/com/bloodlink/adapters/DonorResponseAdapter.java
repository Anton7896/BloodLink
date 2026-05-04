package com.bloodlink.adapters;

import android.view.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;
import com.bloodlink.data.database.entities.DonorResponse;
import com.bloodlink.databinding.ItemDonorResponseBinding;

public class DonorResponseAdapter extends ListAdapter<DonorResponse, DonorResponseAdapter.VH> {

    public interface OnAction { void act(int id); }
    public interface OnDonated { void act(int responseId, int requestId); }

    private final OnAction onConfirm, onReject;
    private final OnDonated onDonated;

    public DonorResponseAdapter(OnAction onConfirm, OnAction onReject, OnDonated onDonated) {
        super(DIFF);
        this.onConfirm = onConfirm;
        this.onReject  = onReject;
        this.onDonated = onDonated;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(ItemDonorResponseBinding.inflate(LayoutInflater.from(p.getContext()), p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) { h.bind(getItem(pos)); }

    class VH extends RecyclerView.ViewHolder {
        final ItemDonorResponseBinding b;
        VH(ItemDonorResponseBinding b) { super(b.getRoot()); this.b = b; }

        void bind(DonorResponse r) {
            b.tvDonorId.setText("Донор #" + r.donorId);
            b.tvMessage.setText(r.message.isEmpty() ? "Без съобщение" : r.message);
            b.tvStatus.setText(r.getStatusDisplay());

            boolean isPending   = DonorResponse.STATUS_PENDING.equals(r.status);
            boolean isConfirmed = DonorResponse.STATUS_CONFIRMED.equals(r.status);

            b.btnConfirm.setVisibility(isPending ? View.VISIBLE : View.GONE);
            b.btnReject.setVisibility(isPending  ? View.VISIBLE : View.GONE);
            b.btnDonated.setVisibility(isConfirmed ? View.VISIBLE : View.GONE);

            b.btnConfirm.setOnClickListener(v -> onConfirm.act(r.id));
            b.btnReject.setOnClickListener(v  -> onReject.act(r.id));
            b.btnDonated.setOnClickListener(v -> onDonated.act(r.id, r.requestId));
        }
    }

    static final DiffUtil.ItemCallback<DonorResponse> DIFF = new DiffUtil.ItemCallback<DonorResponse>() {
        @Override public boolean areItemsTheSame(@NonNull DonorResponse o, @NonNull DonorResponse n) { return o.id == n.id; }
        @Override public boolean areContentsTheSame(@NonNull DonorResponse o, @NonNull DonorResponse n) { return o.status.equals(n.status); }
    };
}
