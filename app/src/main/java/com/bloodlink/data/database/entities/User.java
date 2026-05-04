package com.bloodlink.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String firstName;
    public String lastName;
    public String email;
    public String phone;
    public String passwordHash;   // SHA-256
    public String bloodType;      // "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
    public String city;
    public boolean isAvailableToDonate;
    public double lat;
    public double lng;
    public int donationCount;
    public float rating;
    public int ratingCount;
    public long registeredAt;


    public static final String[] BLOOD_TYPES =
            {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    // Нива на донора
    public static final int LEVEL_NEW    = 0;  // 0-2 дарявания
    public static final int LEVEL_ACTIVE = 1;  // 3-9 дарявания
    public static final int LEVEL_HERO   = 2;  // 10+ дарявания

    public User(String firstName, String lastName, String email,
                String phone, String passwordHash, String bloodType, String city) {
        this.firstName        = firstName;
        this.lastName         = lastName;
        this.email            = email;
        this.phone            = phone;
        this.passwordHash     = passwordHash;
        this.bloodType        = bloodType;
        this.city             = city;
        this.isAvailableToDonate = true;
        this.donationCount    = 0;
        this.rating           = 0f;
        this.ratingCount      = 0;
        this.registeredAt     = System.currentTimeMillis();
    }

    // Кръвна съвместимост

    public static boolean canDonateTo(String donorType, String recipientType) {
        if (donorType == null || recipientType == null) return false;
        switch (recipientType) {
            case "AB+": return true; // Универсален получател взима от всички
            case "AB-": return donorType.equals("AB-") || donorType.equals("A-")
                             || donorType.equals("B-")  || donorType.equals("O-");
            case "A+" : return donorType.equals("A+")  || donorType.equals("A-")
                             || donorType.equals("O+")  || donorType.equals("O-");
            case "A-" : return donorType.equals("A-")  || donorType.equals("O-");
            case "B+" : return donorType.equals("B+")  || donorType.equals("B-")
                             || donorType.equals("O+")  || donorType.equals("O-");
            case "B-" : return donorType.equals("B-")  || donorType.equals("O-");
            case "O+" : return donorType.equals("O+")  || donorType.equals("O-");
            case "O-" : return donorType.equals("O-"); // Само от O (универсален донор)
            default   : return false;
        }
    }


    public int getDonorLevel() {
        if (donationCount >= 10) return LEVEL_HERO;
        if (donationCount >= 3)  return LEVEL_ACTIVE;
        return LEVEL_NEW;
    }

    public String getDonorLevelName() {
        switch (getDonorLevel()) {
            case LEVEL_HERO:   return "🦸 Герой";
            case LEVEL_ACTIVE: return "⭐ Активен донор";
            default:           return "🆕 Нов донор";
        }
    }

    public String getProgressToNextLevel() {
        if (donationCount >= 10) return "Максимално ниво! ❤️";
        if (donationCount >= 3)  return "Още " + (10 - donationCount) + " до Герой 🦸";
        return "Още " + (3 - donationCount) + " до Активен донор ⭐";
    }


    public String getFullName() { return firstName + " " + lastName; }

    public String getDisplayRating() {
        if (ratingCount == 0) return "Без оценки";
        return String.format("★ %.1f (%d оценки)", rating, ratingCount);
    }
}
