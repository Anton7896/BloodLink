package com.bloodlink.data.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(
    tableName = "blood_requests",
    foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "id",
        childColumns = "requesterId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = @Index("requesterId")
)
public class BloodRequest {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int    requesterId;
    public String bloodType;
    public String hospital;
    public String city;
    public String description;
    public String urgencyLevel;    // "CRITICAL", "HIGH", "NORMAL"
    public int    unitsNeeded;     // (1-5)
    public String status;          // "OPEN", "FULFILLED", "CLOSED"
    public long   createdAt;
    public double lat;
    public double lng;
    public String patientName;
    public String contactPhone;


    public static final String STATUS_OPEN      = "OPEN";
    public static final String STATUS_FULFILLED = "FULFILLED";
    public static final String STATUS_CLOSED    = "CLOSED";


    public static final String URGENCY_CRITICAL = "CRITICAL";
    public static final String URGENCY_HIGH     = "HIGH";
    public static final String URGENCY_NORMAL   = "NORMAL";

    public BloodRequest() {
        this.status    = STATUS_OPEN;
        this.createdAt = System.currentTimeMillis();
        this.unitsNeeded = 1;
        this.urgencyLevel = URGENCY_HIGH;
    }

    public String getUrgencyDisplay() {
        switch (urgencyLevel) {
            case URGENCY_CRITICAL: return "🔴 КРИТИЧНО";
            case URGENCY_HIGH:     return "🟠 Спешно";
            default:               return "🟡 Обикновено";
        }
    }

    public String getStatusDisplay() {
        switch (status) {
            case STATUS_FULFILLED: return "✅ Изпълнено";
            case STATUS_CLOSED:    return "⛔ Затворено";
            default:               return "🩸 Търси се";
        }
    }
}
