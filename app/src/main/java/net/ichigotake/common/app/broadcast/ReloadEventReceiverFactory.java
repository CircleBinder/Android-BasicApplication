package net.ichigotake.common.app.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public abstract class ReloadEventReceiverFactory implements BroadcastReceiverFactory {

    public static String ACTION = "circlebinder.creation.app";

    public static Intent createBroadcastIntent() {
        return new Intent(ACTION);
    }

    @Override
    public BroadcastReceiver createBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                callback();
            }
        };
    }
    
    public abstract void callback();

    @Override
    public IntentFilter createIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        return filter;
    }
}
