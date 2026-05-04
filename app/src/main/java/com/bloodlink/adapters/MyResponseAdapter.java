package com.bloodlink.adapters;

import android.view.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;
import com.bloodlink.data.database.entities.DonorResponse;
import com.bloodlink.databinding.ItemMyResponseBinding;
import java.text.SimpleDateFormat;
import java.util.*;

public class MyResponseAdapter extends ListAdapter<DonorResponse, MyResponseAdapter.VH> {

    public MyResponseAdapter() { super(DIFF); }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(ItemMyResponseBinding.inflate(LayoutInflater.from(p.getContext()), p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) { h.bind(getItem(pos)); }

    static class VH extends RecyclerView.ViewHolder {
        final ItemMyResponseBinding b;
        VH(ItemMyResponseBinding b) { super(b.getRoot()); this.b = b; }

        void bind(DonorResponse r) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", new Locale("bg"));
            b.tvRequestId.setText("Заявка #" + r.requestId);
            b.tvStatus.setText(r.getStatusDisplay());
            b.tvDate.setText("Откликнал на: " + sdf.format(new Date(r.respondedAt)));
            b.tvMessage.setText(r.message.isEmpty() ? "Без съобщение" : r.message);
        }
    }

    static final DiffUtil.ItemCallback<DonorResponse> DIFF = new DiffUtil.ItemCallback<DonorResponse>() {
        @Override public boolean areItemsTheSame(@NonNull DonorResponse o, @NonNull DonorResponse n) { return o.id == n.id; }
        @Override public boolean areContentsTheSame(@NonNull DonorResponse o, @NonNull DonorResponse n) { return o.status.equals(n.status); }
    };
}
