package com.bloodlink.utils;

import android.app.*;
import android.content.*;
import androidx.core.app.*;
import com.bloodlink.R;
import com.bloodlink.ui.main.MainActivity;

public class NotificationHelper {

    private static final String CHANNEL_ID   = "bloodlink_channel";
    private static final String CHANNEL_NAME = "BloodLink Известия";

    public static void createChannel(Context ctx) {
        NotificationChannel ch = new NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        ch.setDescription("Спешни известия за нужда от кръв");
        ((NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE))
                .createNotificationChannel(ch);
    }


    public static void notifyDonorNeeded(Context ctx, String bloodType, String hospital, String city) {
        send(ctx,
             "🩸 Спешна нужда от кръв " + bloodType + "!",
             hospital + ", " + city + " — Можете ли да помогнете?");
    }


    public static void notifyResponseConfirmed(Context ctx, String hospital) {
        send(ctx, "✅ Откликът ви е потвърден!",
             "Моля, свържете се с " + hospital + " за уточнения.");
    }


    public static void notifyNewDonorResponse(Context ctx, String donorName, String bloodType) {
        send(ctx, "🩸 Намерен донор!",
             donorName + " (" + bloodType + ") иска да дари кръв.");
    }


    public static void notifyDonationComplete(Context ctx, String requesterName) {
        send(ctx, "💙 Благодарим ви!",
             "Помогнахте на " + requesterName + ". Героят сте вие!");
    }

    private static void send(Context ctx, String title, String body) {
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(ctx, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_blood_drop)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pi);
        try {
            NotificationManagerCompat.from(ctx).notify((int) System.currentTimeMillis(), b.build());
        } catch (SecurityException ignored) {}
    }
}
