package circlebinder.common.app.phone;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import net.ichigotake.common.app.ActivityNavigation;
import net.ichigotake.common.app.ActivityTripper;
import net.ichigotake.common.app.BaseActivity;
import net.ichigotake.common.app.IntentFactory;
import net.ichigotake.common.app.OpenLinkIntentFactory;
import net.ichigotake.common.app.TextShareIntentFactory;
import net.ichigotake.common.content.OnBeforeLoadingListener;
import net.ichigotake.common.os.BundleMerger;
import net.ichigotake.common.rx.Binder;
import net.ichigotake.common.rx.ObservableBuilder;
import net.ichigotake.common.util.Finders;
import net.ichigotake.common.view.ActionProvider;
import net.ichigotake.common.view.MenuPresenter;
import net.ichigotake.common.view.ReloadActionProvider;

import java.util.concurrent.Callable;

import circlebinder.common.checklist.ChecklistSelectActionProvider;
import circlebinder.common.circle.CircleDetailHeaderView;
import circlebinder.common.circle.CircleWebView;
import circlebinder.common.event.Circle;
import circlebinder.common.flow.Screen;
import circlebinder.common.flow.ScreenFlow;
import circlebinder.common.search.CircleSearchOption;
import circlebinder.R;
import circlebinder.common.table.EventCircleTable;
import circlebinder.common.table.SQLite;
import circlebinder.common.web.WebViewClient;
import flow.Backstack;
import flow.Flow;
import flow.Layout;
import rx.Observable;
import rx.android.lifecycle.LifecycleObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class CircleDetailActivity extends BaseActivity implements Binder<Circle> {

    private static final String EXTRA_KEY_SEARCH_OPTION = "search_option";
    private static final String EXTRA_KEY_POSITION = "position";

    public static IntentFactory from(final CircleSearchOption searchOption, final int position) {
        return new IntentFactory() {
            @Override
            public Intent createIntent(Context context) {
                Intent intent = new Intent(context, CircleDetailActivity.class);
                Bundle extras = new Bundle();
                extras.putParcelable(EXTRA_KEY_SEARCH_OPTION, searchOption);
                extras.putInt(EXTRA_KEY_POSITION, position);
                intent.putExtras(extras);
                return intent;
            }
        };
    }

    private CircleSearchOption searchOption;
    private int currentPosition;
    private CircleDetailHeaderView actionBarHeaderView;
    private String currentUrl;
    private CircleWebView webView;
    private Circle circle;

    @Override
    protected ScreenFlow createScreenFlow() {
        return new ScreenFlow() {
            @Override
            public Screen createFirstScreen() {
                return new CircleDetailScreen();
            }

            @Override
            public void onAfterGoing(Backstack nextBackstack, Flow.Direction direction, Flow.Callback callback) {

            }
        };
    }

    @Layout(R.layout.common_activity_circle_detail)
    private static class CircleDetailScreen extends Screen {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBarHeaderView = new CircleDetailHeaderView(this);
        actionBar.setCustomView(actionBarHeaderView);

        Bundle bundle = BundleMerger.merge(getIntent(), savedInstanceState);
        searchOption = bundle.getParcelable(EXTRA_KEY_SEARCH_OPTION);
        currentPosition = bundle.getInt(EXTRA_KEY_POSITION);

        webView = Finders.from(this).findOrNull(R.id.common_activity_circle_detail_web_view);
        WebViewClient client = new WebViewClient(webView);
        client.setOnBeforeLoadingListener(new OnBeforeLoadingListener() {
            @Override
            public void onBeforeLoading(String url) {
                currentUrl = url;
                actionBar.invalidateOptionsMenu();
            }
        });
        webView.setWebViewClient(client);

        LifecycleObservable.bindActivityLifecycle(lifecycle(), createObservable());
    }

    @Override
    public void bind(Circle item) {
        this.circle = item;
        this.actionBarHeaderView.setCircle(circle);
        invalidateOptionsMenu();
    }

    private Observable<Circle> createObservable() {
        return ObservableBuilder
                .from(new CircleSearcher(SQLite.getDatabase(this), searchOption))
                .bind(this)
                .bind(webView)
                .createObservable()
                .observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuPresenter presenter = new MenuPresenter(menu, getMenuInflater());
        MenuItem shareItem = presenter.inflate(R.menu.share, R.id.menu_share);
        presenter.setActionProvider(shareItem, new ActionProvider(this, new ActionProvider.OnClickListener() {
            @Override
            public void onClick() {
                ActivityTripper
                        .from(CircleDetailActivity.this, new TextShareIntentFactory(circle.getName(), currentUrl))
                        .trip();
            }
        }));
        presenter.setShowAsAction(shareItem, MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        MenuItem openBrowserItem = presenter.inflate(R.menu.open_browser, R.id.menu_open_browser);
        presenter.setActionProvider(openBrowserItem, new ActionProvider(this, new ActionProvider.OnClickListener() {
            @Override
            public void onClick() {
                ActivityTripper.from(getApplicationContext(), new OpenLinkIntentFactory(currentUrl)).trip();
            }
        }));
        presenter.setShowAsAction(openBrowserItem, MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        MenuItem reloadItem = presenter.inflate(R.menu.reload, R.id.menu_reload);
        presenter.setActionProvider(reloadItem, new ReloadActionProvider(this, webView));
        presenter.setShowAsAction(reloadItem, MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        MenuItem selectorItem = presenter.inflate(R.menu.checklist_selector, R.id.menu_checklist_selector);
        if (circle != null) {
            selectorItem.setVisible(true);
            presenter.setActionProvider(selectorItem, new ChecklistSelectActionProvider(this, circle));
        } else {
            selectorItem.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return ActivityNavigation.back(this, menuItem)
                || super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_KEY_SEARCH_OPTION, searchOption);
        outState.putInt(EXTRA_KEY_POSITION, currentPosition);
    }

    private static class CircleSearcher implements Callable<Circle> {

        private final EventCircleTable eventCircleTable;
        private final CircleSearchOption searchOption;

        private CircleSearcher(SQLiteDatabase database, CircleSearchOption searchOption) {
            this.eventCircleTable = new EventCircleTable(database);
            this.searchOption = searchOption;
        }

        @Override
        public Circle call() throws Exception {
            for (Circle circle : eventCircleTable.findOne(searchOption).asSet()) {
                return circle;
            }
            throw new IllegalStateException();
        }
    }

}
