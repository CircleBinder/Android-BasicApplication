package circlebinder.creation.home;

import android.content.Context;

import net.ichigotake.common.app.IntentFactory;

import circlebinder.R;
import circlebinder.common.app.phone.WebViewActivity;
import circlebinder.common.card.HomeCard;

public final class CreationHomepageCard implements HomeCard {

    private final CharSequence label;
    private final CharSequence caption;

    public CreationHomepageCard(Context context) {
        this.label = context.getString(R.string.app_event_name_short);
        this.caption = context.getString(R.string.app_event_official_site_label);
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
        return R.color.app_color_primary;
    }

    @Override
    public IntentFactory createTransitIntentFactory(Context context) {
        return WebViewActivity.from(context.getString(R.string.app_event_official_site_url));
    }
}
