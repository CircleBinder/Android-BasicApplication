package circlebinder.common.app;


import android.app.Application;
import android.os.AsyncTask;

import java.util.Locale;

import circlebinder.common.table.SQLite;

public class CircleBinderApplication extends Application {

    public static CircleBinderApplication get() {
        return instance;
    }

    public static final Locale APP_LOCALE = Locale.JAPAN;
    private static CircleBinderApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CrashReporter.onStart(getApplicationContext());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SQLite.getReadableDatabase(getApplicationContext());
            }
        });
    }

    @Override
    public void onTerminate() {
        instance = null;
        super.onTerminate();
    }

}
