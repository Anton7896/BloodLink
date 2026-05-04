package com.bloodlink.data.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "ratings",
    foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "id",
            childColumns = "ratedUserId", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = User.class, parentColumns = "id",
            childColumns = "raterUserId", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = BloodRequest.class, parentColumns = "id",
            childColumns = "requestId", onDelete = ForeignKey.CASCADE)
    },
    indices = { @Index("ratedUserId"), @Index("raterUserId"), @Index("requestId") }
)
public class Rating {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int    ratedUserId;
    public int    raterUserId;
    public int    requestId;
    public int    stars;         // 1-5
    public String comment;
    public long   createdAt;

    public Rating() {
        this.createdAt = System.currentTimeMillis();
        this.comment   = "";
    }
}
