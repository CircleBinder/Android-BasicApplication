package circlebinder.common.card;

import android.content.Context;

import net.ichigotake.common.app.IntentFactory;

public interface HomeCard {

    CharSequence getLabel();

    CharSequence getCaption();

    int getBackgroundResource();

    IntentFactory createTransitIntentFactory(Context context);

}
