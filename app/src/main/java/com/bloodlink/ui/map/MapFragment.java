package com.bloodlink.ui.map;

import android.os.Bundle;
import android.view.*;
import android.graphics.*;
import android.graphics.drawable.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.*;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import com.bloodlink.data.database.AppDatabase;
import com.bloodlink.data.database.entities.BloodRequest;
import com.bloodlink.databinding.FragmentMapBinding;
import com.bloodlink.utils.AppExecutors;
import com.bloodlink.utils.SessionManager;

import java.util.List;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private MapView mapView;
    private AppDatabase db;

    @Override
    public View onCreateView(@NonNull LayoutInflater i, ViewGroup c, Bundle s) {
        binding = FragmentMapBinding.inflate(i, c, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = AppDatabase.getInstance(requireContext());


        Configuration.getInstance().setUserAgentValue("BloodLink/1.0");

        mapView = binding.mapView;
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(7.5);
        // Центриране над България
        mapView.getController().setCenter(new GeoPoint(42.73, 25.48));


        loadRequestsOnMap();

        binding.btnCenter.setOnClickListener(v -> {
            mapView.getController().animateTo(new GeoPoint(42.73, 25.48));
            mapView.getController().setZoom(7.5);
        });
    }

    private void loadRequestsOnMap() {
        db.bloodRequestDao().observeOpenRequests().observe(getViewLifecycleOwner(), requests -> {
            mapView.getOverlays().clear();

            if (requests == null || requests.isEmpty()) {
                binding.tvNoRequests.setVisibility(View.VISIBLE);
                return;
            }
            binding.tvNoRequests.setVisibility(View.GONE);
            binding.tvRequestCount.setText("🩸 " + requests.size() + " активни заявки");

            for (BloodRequest req : requests) {
                if (req.lat == 0 && req.lng == 0) continue;
                addMarker(req);
            }
            mapView.invalidate();
        });
    }

    private void addMarker(BloodRequest req) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(req.lat, req.lng));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        marker.setTitle(req.bloodType + " — " + req.hospital);
        marker.setSnippet(req.getUrgencyDisplay() + "\n📍 " + req.city
                + "\nЕдиници: " + req.unitsNeeded
                + (req.contactPhone != null && !req.contactPhone.isEmpty()
                    ? "\n📞 " + req.contactPhone : ""));

        // Цветен маркер спрямо спешност
        marker.setIcon(buildMarkerIcon(req));

        marker.setOnMarkerClickListener((m, map) -> {
            m.showInfoWindow();
            return true;
        });

        mapView.getOverlays().add(marker);
    }


    private Drawable buildMarkerIcon(BloodRequest req) {
        int color;
        switch (req.urgencyLevel) {
            case BloodRequest.URGENCY_CRITICAL: color = Color.rgb(183, 28,  28);  break;
            case BloodRequest.URGENCY_HIGH:     color = Color.rgb(230, 81,   0);  break;
            default:                            color = Color.rgb(229, 57,  53);
        }

        int size = 120;
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(color);
        circlePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4, circlePaint);


        circlePaint.setColor(Color.WHITE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(6f);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 6, circlePaint);


        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextSize(req.bloodType.length() > 2 ? 30f : 36f);
        float textY = size / 2f - ((textPaint.descent() + textPaint.ascent()) / 2);
        canvas.drawText(req.bloodType, size / 2f, textY, textPaint);

        return new BitmapDrawable(getResources(), bmp);
    }

    @Override
    public void onResume()  { super.onResume();  if (mapView != null) mapView.onResume(); }
    @Override
    public void onPause()   { super.onPause();   if (mapView != null) mapView.onPause(); }
    @Override
    public void onDestroyView() { super.onDestroyView(); binding = null; }
}
