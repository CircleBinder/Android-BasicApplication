package circlebinder.creation.app.phone;

import android.content.Intent;
import android.os.Bundle;

import net.ichigotake.common.app.ActivityTripper;

import net.ichigotake.common.rx.RxActionBarActivity;
import circlebinder.creation.initialize.LegacyAppStorage;
import circlebinder.creation.initialize.LegacyStorageSweeper;

public final class LauncherActivity extends RxActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new LegacyStorageSweeper(this).sweep();
        Intent launchActivity;
        if (new LegacyAppStorage(getApplicationContext()).isInitialized()) {
            launchActivity = HomeActivity.createIntent(this);
        } else {
            launchActivity = DatabaseInitializeActivity.createIntent(this);
        }
        new ActivityTripper(this, launchActivity)
                .withFinish()
                .trip();
        super.onCreate(savedInstanceState);
    }
}
