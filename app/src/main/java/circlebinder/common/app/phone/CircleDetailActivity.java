package circlebinder.common.app.phone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dmitriy.tarasov.android.intents.IntentUtils;

import net.ichigotake.common.app.ActivityNavigation;
import net.ichigotake.common.app.ActivityTripper;
import net.ichigotake.common.app.IntentFactory;
import net.ichigotake.common.app.TextShareIntentFactory;
import net.ichigotake.common.content.OnBeforeLoadingListener;
import net.ichigotake.common.os.BundleMerger;
import net.ichigotake.common.util.Finders;
import net.ichigotake.common.util.Optional;
import net.ichigotake.common.view.ActionProvider;
import net.ichigotake.common.view.MenuPresenter;
import net.ichigotake.common.view.ReloadActionProvider;

import circlebinder.common.Legacy;
import net.ichigotake.common.rx.RxActionBarActivity;
import circlebinder.common.checklist.ChecklistSelectActionProvider;
import circlebinder.common.circle.CircleDetailHeaderView;
import circlebinder.common.circle.CircleWebView;
import circlebinder.common.event.Circle;
import circlebinder.common.search.CircleSearchOption;
import circlebinder.R;
import circlebinder.common.table.EventCircleTable;
import circlebinder.common.table.SQLite;
import circlebinder.common.web.WebViewClient;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class CircleDetailActivity extends RxActionBarActivity implements Legacy {

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.common_activity_circle_detail);

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

        AndroidObservable.bindActivity(this, Observable.create(new Observable.OnSubscribe<Circle>() {
            @Override
            public void call(Subscriber<? super Circle> subscriber) {
                Optional<Circle> circle = new EventCircleTable(SQLite.getDatabase(getApplicationContext()))
                        .findOne(searchOption);
                if (circle.isPresent()) {
                    subscriber.onNext(circle.get());
                } else {
                    subscriber.onError(new IllegalStateException());
                }
            }
        }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<Circle>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("CircleDetail", "", e);
                    }

                    @Override
                    public void onNext(Circle circle) {
                        Log.d("CircleDetail", "cirlce " + circle.getName());
                        CircleDetailActivity.this.circle = circle;
                        webView.setCircle(circle);
                        postEvent();
                    }
                });
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
                new ActivityTripper(getApplicationContext(), IntentUtils.openLink(currentUrl)).trip();
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

    private void onCirclePageChanged(Circle circle) {
        this.actionBarHeaderView.setCircle(circle);
        invalidateOptionsMenu();
    }

    private void postEvent() {
        onCirclePageChanged(circle);
    }

}
