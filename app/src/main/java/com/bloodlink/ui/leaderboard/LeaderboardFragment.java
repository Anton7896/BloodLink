package com.bloodlink.ui.leaderboard;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.*;
import com.bloodlink.R;
import com.bloodlink.data.database.AppDatabase;
import com.bloodlink.data.database.entities.User;
import com.bloodlink.databinding.FragmentLeaderboardBinding;
import com.bloodlink.utils.AppExecutors;
import com.bloodlink.utils.SessionManager;

import java.util.List;

public class LeaderboardFragment extends Fragment {

    private FragmentLeaderboardBinding binding;
    private AppDatabase db;
    private SessionManager session;

    @Override
    public View onCreateView(@NonNull LayoutInflater i, ViewGroup c, Bundle s) {
        binding = FragmentLeaderboardBinding.inflate(i, c, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db      = AppDatabase.getInstance(requireContext());
        session = new SessionManager(requireContext());


        db.userDao().getTopDonors().observe(getViewLifecycleOwner(), users -> {
            updateLeaderboard(users);
        });


        db.userDao().getActiveDonorsCount().observe(getViewLifecycleOwner(), count -> {
            binding.tvActiveDonors.setText(String.valueOf(count != null ? count : 0));
        });

        db.userDao().getTotalDonations().observe(getViewLifecycleOwner(), total -> {
            binding.tvTotalDonations.setText(String.valueOf(total != null ? total : 0));
        });

        db.bloodRequestDao().observeOpenRequests().observe(getViewLifecycleOwner(), reqs -> {
            binding.tvOpenRequests.setText(String.valueOf(reqs != null ? reqs.size() : 0));
        });
    }

    private void updateLeaderboard(List<User> users) {
        binding.leaderboardContainer.removeAllViews();
        if (users == null || users.isEmpty()) {
            binding.tvEmptyLeaderboard.setVisibility(View.VISIBLE);
            return;
        }
        binding.tvEmptyLeaderboard.setVisibility(View.GONE);

        int myId = session.getUserId();

        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            View row = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_leaderboard_row, binding.leaderboardContainer, false);


            TextView tvRank = row.findViewById(R.id.tvRank);
            String rankEmoji = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : String.valueOf(i + 1);
            tvRank.setText(rankEmoji);


            TextView tvLevel = row.findViewById(R.id.tvLevel);
            tvLevel.setText(u.getDonorLevelName());

            // Кръвна група
            TextView tvBlood = row.findViewById(R.id.tvBlood);
            tvBlood.setText(u.bloodType);

            //подчертай
            TextView tvName = row.findViewById(R.id.tvName);
            tvName.setText(u.getFullName() + (u.id == myId ? " (Ти)" : ""));
            if (u.id == myId) {
                row.setBackgroundColor(0x22B71C1C);
                tvName.setTextColor(0xFFB71C1C);
            }

            TextView tvCity = row.findViewById(R.id.tvCity);
            tvCity.setText(u.city);

            TextView tvDonations = row.findViewById(R.id.tvDonations);
            tvDonations.setText(u.donationCount + " 🩸");

            binding.leaderboardContainer.addView(row);
        }
    }

    @Override
    public void onDestroyView() { super.onDestroyView(); binding = null; }
}
