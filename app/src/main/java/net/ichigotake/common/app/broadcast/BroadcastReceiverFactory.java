package net.ichigotake.common.app.broadcast;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public interface BroadcastReceiverFactory {
    
    BroadcastReceiver createBroadcastReceiver();
    
    IntentFilter createIntentFilter();

}
