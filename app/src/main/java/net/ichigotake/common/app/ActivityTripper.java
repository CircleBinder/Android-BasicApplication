package net.ichigotake.common.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * アクティビティの遷移をする
 */
public class ActivityTripper implements Tripper {
    
    public static ActivityTripper from(Context context, IntentFactory intentFactory) {
        return new ActivityTripper(context, intentFactory.createIntent(context));
    }

    private final String LOG_TAG = ActivityTripper.class.getSimpleName();
    private final Context context;
    private final Intent intent;
    private boolean withFinish;

    /**
     * @deprecated 下位互換性を維持するためのもの。クラス外から使われなくなったら private にする
     */
    @Deprecated
    public ActivityTripper(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    public ActivityTripper withFinish() {
        withFinish = true;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return this;
    }

    public ActivityTripper newTask() {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return this;
    }

    @Override
    public void trip() {
        if (context == null) {
            Log.d(LOG_TAG, "context is null.");
            return ;
        }

        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        if (withFinish && context instanceof Activity) {
            ((Activity)context).finish();
        }
    }
}
