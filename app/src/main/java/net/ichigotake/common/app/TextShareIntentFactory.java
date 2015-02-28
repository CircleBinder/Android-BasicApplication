package net.ichigotake.common.app;

import android.content.Context;
import android.content.Intent;

import com.dmitriy.tarasov.android.intents.IntentUtils;

public class TextShareIntentFactory implements IntentFactory {

    private final String subject;
    private final String text;

    public TextShareIntentFactory(String subject, String text) {
        this.subject = subject;
        this.text = text;
    }

    @Override
    public Intent createIntent(Context context) {
        return IntentUtils.shareText(subject, text);
    }

}
