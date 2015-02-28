package net.ichigotake.common.app;

import android.content.Context;
import android.content.Intent;

import com.dmitriy.tarasov.android.intents.IntentUtils;

public class OpenLinkIntentFactory implements IntentFactory {

    private final String url;

    public OpenLinkIntentFactory(String url) {
        this.url = url;
    }

    @Override
    public Intent createIntent(Context context) {
        return IntentUtils.openLink(url);
    }

}
