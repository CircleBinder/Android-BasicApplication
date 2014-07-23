package circlebinder.creation.app.phone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import net.ichigotake.common.app.ActivityFactory;
import net.ichigotake.common.app.ActivityNavigation;
import net.ichigotake.common.app.ActivityTripper;

import circlebinder.creation.app.BaseActivity;
import circlebinder.R;

public final class AboutActivity extends BaseActivity {

    public static ActivityTripper tripper(Context context) {
        return new ActivityTripper(context, factory());
    }

    public static ActivityFactory factory() {
        return new ActivityFactory() {
            @Override
            public Intent create(Context context) {
                return new Intent(context, AboutActivity.class);
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_about);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return ActivityNavigation.back(this, item)
                || super.onOptionsItemSelected(item);
    }

}
