package circlebinder.creation.initialize;

import android.content.Context;

public class LegacyStorageSweeper {
    
    private final Context context;

    public LegacyStorageSweeper(Context context) {
        this.context = context;
    }

    public void sweep() {
        new LegacyAppStorage(context).reset();
        context.deleteDatabase("Creation.db");
    }
    
}
