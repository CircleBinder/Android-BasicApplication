package net.ichigotake.common.app.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class BroadcastLifecycle {

    private final List<BroadcastReceiver> receivers = new ArrayList<>();
    private WeakReference<Context> context;
    
    public void registerReceiver(BroadcastReceiverFactory broadcastReceiverFactory) {
        if (this.context.isEnqueued()) {
            return;
        }
        Context context = this.context.get();

        BroadcastReceiver receiver = broadcastReceiverFactory.createBroadcastReceiver();
        context.registerReceiver(
                broadcastReceiverFactory.createBroadcastReceiver(), broadcastReceiverFactory.createIntentFilter());
        receivers.add(receiver);
    }

    /**
     * For {@link Context} lifecycles.
     */
    public void onCreate(Context context) {
        this.context = new WeakReference<>(context);
    }

    /**
     * For {@link Context} lifecycles.
     */
    public void onDestroy() {
        if (this.context.isEnqueued()) {
            return;
        }
        Context context = this.context.get();
        
        for (BroadcastReceiver receiver : receivers) {
            context.unregisterReceiver(receiver);
        }
        receivers.clear();
    }

}
