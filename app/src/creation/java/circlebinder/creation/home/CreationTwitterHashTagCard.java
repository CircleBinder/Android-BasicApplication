package circlebinder.creation.home;

import android.content.Context;

import net.ichigotake.common.app.IntentFactory;
import net.ichigotake.common.app.OpenLinkIntentFactory;

import circlebinder.R;
import circlebinder.common.card.HomeCard;

public final class CreationTwitterHashTagCard implements HomeCard {

    private final CharSequence label;
    private final CharSequence caption;

    public CreationTwitterHashTagCard(Context context) {
        this.label = context.getString(R.string.app_event_twitter_label);
        this.caption = context.getString(R.string.app_event_twitter_hash_tag_label);
    }

    @Override
    public CharSequence getLabel() {
        return label;
    }

    @Override
    public CharSequence getCaption() {
        return caption;
    }

    @Override
    public int getBackgroundResource() {
        return R.color.common_card_twitter_background;
    }

    @Override
    public IntentFactory createTransitIntentFactory(Context context) {
        return new OpenLinkIntentFactory(context.getString(R.string.app_event_twitter_hash_tag_url));
    }
}
