package circlebinder.creation.app.phone;

import android.os.Bundle;

import net.ichigotake.common.app.ActivityTripper;

import net.ichigotake.common.app.BaseActivity;
import net.ichigotake.common.app.IntentFactory;

import circlebinder.creation.initialize.LegacyAppStorage;
import circlebinder.creation.initialize.LegacyStorageSweeper;

public final class LauncherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new LegacyStorageSweeper(this).sweep();
        IntentFactory launchActivity;
        if (new LegacyAppStorage(getApplicationContext()).isInitialized()) {
            launchActivity = HomeActivity.from();
        } else {
            launchActivity = DatabaseInitializeActivity.from();
        }
        ActivityTripper
                .from(this, launchActivity)
                .withFinish()
                .trip();
        super.onCreate(savedInstanceState);
    }
}
