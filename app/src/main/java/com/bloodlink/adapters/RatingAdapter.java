package com.bloodlink.adapters;

import android.view.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;
import com.bloodlink.data.database.entities.Rating;
import com.bloodlink.databinding.ItemRatingBinding;
import java.text.SimpleDateFormat;
import java.util.*;

public class RatingAdapter extends ListAdapter<Rating, RatingAdapter.VH> {

    public RatingAdapter() { super(DIFF); }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(ItemRatingBinding.inflate(LayoutInflater.from(p.getContext()), p, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) { h.bind(getItem(pos)); }

    static class VH extends RecyclerView.ViewHolder {
        final ItemRatingBinding b;
        VH(ItemRatingBinding b) { super(b.getRoot()); this.b = b; }

        void bind(Rating r) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", new Locale("bg"));
            b.tvStars.setText("★".repeat(r.stars) + "☆".repeat(5 - r.stars));
            b.tvComment.setText(r.comment.isEmpty() ? "Без коментар" : r.comment);
            b.tvDate.setText(sdf.format(new Date(r.createdAt)));
            b.tvRater.setText("Потребител #" + r.raterUserId);
        }
    }

    static final DiffUtil.ItemCallback<Rating> DIFF = new DiffUtil.ItemCallback<Rating>() {
        @Override public boolean areItemsTheSame(@NonNull Rating o, @NonNull Rating n) { return o.id == n.id; }
        @Override public boolean areContentsTheSame(@NonNull Rating o, @NonNull Rating n) { return o.stars == n.stars; }
    };
}
