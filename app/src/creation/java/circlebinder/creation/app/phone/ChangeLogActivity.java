package circlebinder.creation.app.phone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import net.ichigotake.common.app.ActivityNavigation;

import java.io.IOException;

import circlebinder.common.app.BaseActivity;
import circlebinder.R;
import circlebinder.common.system.ChangeLogLoader;
import circlebinder.common.system.ChangeLogView;

public final class ChangeLogActivity extends BaseActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, ChangeLogActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creation_activity_change_log);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ChangeLogView changeLogView = (ChangeLogView) findViewById(R.id.creation_activity_change_log);
        try {
            changeLogView.addChangeLogFeedList(new ChangeLogLoader(this).load(R.raw.change_log_ltsv));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return ActivityNavigation.back(this, menuItem)
                || super.onOptionsItemSelected(menuItem);
    }

}
