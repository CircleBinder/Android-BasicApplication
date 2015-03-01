package circlebinder.creation.app.phone;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import net.ichigotake.common.app.BaseActivity;
import net.ichigotake.common.app.IntentFactory;
import net.ichigotake.common.app.broadcast.ReloadEventReceiverFactory;
import net.ichigotake.common.rx.ObservableBuilder;
import net.ichigotake.common.util.ActivityViewFinder;
import net.ichigotake.common.util.Finders;

import java.util.List;

import circlebinder.R;
import circlebinder.common.card.ChecklistCardRetriever;
import circlebinder.common.card.HomeCard;
import circlebinder.common.card.HomeCardAdapter;
import circlebinder.common.table.SQLite;
import circlebinder.creation.home.HomeCardListView;
import circlebinder.creation.system.NavigationDrawerRenderer;
import rx.Observable;
import rx.android.lifecycle.LifecycleObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 通常起動時のファーストビュー
 */
public class HomeActivity extends BaseActivity {

    public static IntentFactory from() {
        return new IntentFactory() {
            @Override
            public Intent createIntent(Context context) {
                return new Intent(context, HomeActivity.class);
            }
        };
    }

    private NavigationDrawerRenderer drawerRenderer;
    private HomeCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creation_activity_home);
        ActivityViewFinder finder = Finders.from(this);
        Toolbar toolbar = finder.findOrNull(R.id.creation_activity_home_toolbar);
        setSupportActionBar(toolbar);
        
        drawerRenderer = new NavigationDrawerRenderer(
                this,
                toolbar,
                finder.<DrawerLayout>findOrNull(R.id.creation_activity_home_container),
                finder.findOrNull(R.id.creation_activity_home_system_menu)
        );
        
        HomeCardListView homeCardListView = finder.findOrNull(R.id.creation_activity_home_checklist_list);
        adapter = new HomeCardAdapter(this);
        homeCardListView.setAdapter(adapter);
        
        LifecycleObservable
                .bindActivityLifecycle(lifecycle(), homeCardsObservable())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<HomeCard>>() {
                    @Override
                    public void call(List<HomeCard> homeCards) {
                        adapter.clear();
                        adapter.addAll(homeCards);
                    }
                });

        registerReceiver(new ReloadEventReceiverFactory() {
            @Override
            public void callback() {
                reload();
            }
        });
    }
    
    private void reload() {
        lifecycle().repeat();
    }
    
    private Observable<List<HomeCard>> homeCardsObservable() {
        return ObservableBuilder
                .from(new ChecklistCardRetriever(SQLite.getDatabase(this)))
                .bind(adapter)
                .create();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerRenderer.onPostCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerRenderer.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerRenderer.onOptionsItemSelected(item)
                || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!drawerRenderer.onBackPressed()) {
            super.onBackPressed();
        }
    }

}

