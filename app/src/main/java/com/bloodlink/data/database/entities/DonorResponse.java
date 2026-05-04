package com.bloodlink.data.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(
    tableName = "donor_responses",
    foreignKeys = {
        @ForeignKey(entity = BloodRequest.class,
            parentColumns = "id", childColumns = "requestId",
            onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = User.class,
            parentColumns = "id", childColumns = "donorId",
            onDelete = ForeignKey.CASCADE)
    },
    indices = { @Index("requestId"), @Index("donorId") }
)
public class DonorResponse {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int    requestId;
    public int    donorId;
    public String status;       // "PENDING", "CONFIRMED", "REJECTED", "DONATED"
    public String message;
    public long   respondedAt;

    public static final String STATUS_PENDING   = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_REJECTED  = "REJECTED";
    public static final String STATUS_DONATED   = "DONATED";

    public DonorResponse() {
        this.status      = STATUS_PENDING;
        this.respondedAt = System.currentTimeMillis();
        this.message     = "";
    }

    public String getStatusDisplay() {
        switch (status) {
            case STATUS_CONFIRMED: return "✅ Потвърден";
            case STATUS_REJECTED:  return "❌ Отхвърлен";
            case STATUS_DONATED:   return "🩸 Дарил е кръв";
            default:               return "⏳ Изчаква";
        }
    }
}
