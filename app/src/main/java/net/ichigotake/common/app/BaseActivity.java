package net.ichigotake.common.app;

import android.os.Bundle;
import android.view.MenuItem;

import net.ichigotake.common.app.broadcast.BroadcastLifecycle;

import net.ichigotake.common.app.broadcast.BroadcastReceiverFactory;
import net.ichigotake.common.util.Finders;

import circlebinder.R;
import circlebinder.common.flow.AppFlow;
import circlebinder.common.flow.FlowBundler;
import circlebinder.common.flow.FrameScreenSwitcherView;
import circlebinder.common.flow.ScreenFlow;
import circlebinder.common.flow.HasTitle;
import circlebinder.common.flow.Screen;
import flow.Backstack;
import flow.Flow;
import rx.android.app.support.RxActionBarActivity;

public abstract class BaseActivity extends RxActionBarActivity implements Flow.Listener {

    private final BroadcastLifecycle broadcastLifecycle = new BroadcastLifecycle();
    private FlowBundler flowBundler;
    private AppFlow appFlow;
    private ScreenFlow screenFlow;
    private FrameScreenSwitcherView container;

    abstract protected ScreenFlow createScreenFlow();
    
    public void registerReceiver(BroadcastReceiverFactory broadcastReceiverFactory) {
        broadcastLifecycle.registerReceiver(broadcastReceiverFactory);
    }

    @Override
    public Object getSystemService(String name) {
        if (AppFlow.isAppFlowSystemService(name)) return appFlow;
        return super.getSystemService(name);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        flowBundler.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (!container.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return (item.getItemId() == android.R.id.home && container.onUpPressed())
                || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        broadcastLifecycle.onCreate(this);

        screenFlow = createScreenFlow();
        flowBundler = new FlowBundler(this, screenFlow);
        appFlow = flowBundler.onCreate(savedInstanceState);
        flowBundler.onCreate(savedInstanceState);
        setContentView(R.layout.common_activity_about);
        container = Finders.from(this).findOrNull(R.id.container);
        AppFlow.loadInitialScreen(this);
    }

    @Override
    protected void onDestroy() {
        broadcastLifecycle.onDestroy();
        super.onDestroy();
    }

    @Override
    public void go(Backstack nextBackstack, Flow.Direction direction, Flow.Callback callback) {
        Screen screen = (Screen)nextBackstack.current().getScreen();
        container.showScreen(screen, direction, callback);
        if (screen instanceof HasTitle) {
            setTitle(((HasTitle) screen).getTitle());
        }
        screenFlow.onAfterGoing(nextBackstack, direction, callback);
    }

}
