package com.bloodlink.utils;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static volatile AppExecutors INSTANCE;
    private final ExecutorService diskIO = Executors.newSingleThreadExecutor();
    private final Handler mainThread = new Handler(Looper.getMainLooper());

    public static AppExecutors getInstance() {
        if (INSTANCE == null) {
            synchronized (AppExecutors.class) {
                if (INSTANCE == null) INSTANCE = new AppExecutors();
            }
        }
        return INSTANCE;
    }

    public void diskIO(Runnable r) { diskIO.execute(r); }
    public void mainThread(Runnable r) { mainThread.post(r); }
}
