package circlebinder.creation.system;

import net.ichigotake.common.app.IntentFactory;

final class SystemMenuItem {

    private final CharSequence label;
    private final int icon;
    private final IntentFactory transitionIntent;

    SystemMenuItem(CharSequence label, int icon, IntentFactory transitionIntent) {
        this.label = label;
        this.icon = icon;
        this.transitionIntent = transitionIntent;
    }

    public CharSequence getLabel() {
        return label;
    }

    public int getIcon() {
        return icon;
    }

    public IntentFactory getTransitionIntent() {
        return transitionIntent;
    }

}
