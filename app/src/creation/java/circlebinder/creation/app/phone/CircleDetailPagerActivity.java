package circlebinder.creation.app.phone;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import net.ichigotake.common.app.ActivityFactory;
import net.ichigotake.common.app.ActivityNavigation;
import net.ichigotake.common.app.ActivityTripper;
import net.ichigotake.common.app.FragmentPagerAdapter;
import net.ichigotake.common.app.FragmentPagerItem;
import net.ichigotake.common.os.BundleMerger;

import circlebinder.Legacy;
import circlebinder.common.event.Circle;
import circlebinder.common.search.CircleSearchOption;
import circlebinder.creation.BaseActivity;
import circlebinder.R;
import circlebinder.creation.circle.CircleDetailFragment;
import circlebinder.creation.circle.CircleDetailViewHolder;
import circlebinder.creation.circle.OnCirclePageChangeListener;
import circlebinder.creation.event.CircleTable;
import circlebinder.creation.search.CircleCursorConverter;

public final class CircleDetailPagerActivity extends BaseActivity
        implements Legacy, OnCirclePageChangeListener {

    private static final String EXTRA_KEY_SEARCH_OPTION = "search_option";
    private static final String EXTRA_KEY_POSITION = "position";

    public static ActivityTripper tripper(Context context, CircleSearchOption searchOption, int position) {
        return new ActivityTripper(context, new CircleDetailPagerActivityFactory(searchOption, position));
    }

    private static class CircleDetailPagerActivityFactory implements ActivityFactory {

        private final CircleSearchOption searchOption;
        private final int position;

        public CircleDetailPagerActivityFactory(CircleSearchOption searchOption, int position) {
            this.searchOption = searchOption;
            this.position = position;
        }

        @Override
        public Intent create(Context context) {
            Intent intent = new Intent(context, CircleDetailPagerActivity.class);
            Bundle extras = new Bundle();
            extras.putParcelable(EXTRA_KEY_SEARCH_OPTION, searchOption);
            extras.putInt(EXTRA_KEY_POSITION, position);
            intent.putExtras(extras);
            return intent;
        }
    }

    private FragmentPagerAdapter pagerAdapter;
    private CircleSearchOption searchOption;
    private int currentPosition;
    private ViewPager pager;
    private View headerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.circlebinder_activity_view_pager);

        headerView = findViewById(R.id.circlebinder_activity_view_pager_header);

        Bundle bundle = BundleMerger.merge(getIntent(), savedInstanceState);
        searchOption = bundle.getParcelable(EXTRA_KEY_SEARCH_OPTION);
        currentPosition = bundle.getInt(EXTRA_KEY_POSITION);

        pager = (ViewPager) findViewById(R.id.circlebinder_activity_view_pager);
        pagerAdapter = new FragmentPagerAdapter(
                getFragmentManager(),
                new CircleDetailPagerItem(searchOption)
        );
        pager.setAdapter(pagerAdapter);
        pager.setPageMargin(getResources().getDimensionPixelSize(
                R.dimen.circlebinder_spacer_small
        ));
        pager.setPageMarginDrawable(new ColorDrawable(
                getResources().getColor(R.color.circlebinder_app_card_container_background)
        ));
        final View forwardView = findViewById(R.id.circlebinder_activity_view_pager_forward);
        forwardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(currentPosition+1);
            }
        });
        final View backView = findViewById(R.id.circlebinder_activity_view_pager_back);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(currentPosition-1);
            }
        });
        final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override public void onPageScrollStateChanged(int state) {}

            @Override
            public void onPageSelected(int position) {
                backView.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
                forwardView.setVisibility(position < pagerAdapter.getCount()-1 ? View.VISIBLE : View.GONE);
                currentPosition = position;
                pagerAdapter.reload(position);
            }
        };
        pager.setOnPageChangeListener(onPageChangeListener);
        pagerAdapter.reload(currentPosition);
        pager.setCurrentItem(currentPosition);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onPageChangeListener.onPageSelected(currentPosition);
            }
        }, 1000);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return ActivityNavigation.back(this, menuItem)
                || super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pager != null) {
            FragmentPagerAdapter pagerAdapter = (FragmentPagerAdapter) pager.getAdapter();
            pagerAdapter.reload(currentPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_KEY_SEARCH_OPTION, searchOption);
        outState.putInt(EXTRA_KEY_POSITION, currentPosition);
    }

    @Override
    public void onCirclePageChanged(Circle circle) {
        CircleDetailViewHolder viewHolder = new CircleDetailViewHolder(headerView);
        viewHolder.getName().setText(circle.getPenName() + "/" + circle.getName());
        viewHolder.getSpace().setText(circle.getSpace().getName());
    }

    private static class CircleDetailPagerItem implements FragmentPagerItem {

        private final CircleCursorConverter cursorCreator;
        private final Cursor cursor;

        public CircleDetailPagerItem(CircleSearchOption circleSearchOption) {
            cursorCreator = new CircleCursorConverter();
            cursor = CircleTable.get(circleSearchOption);
        }

        @Override
        public CircleDetailFragment getItem(int position) {
            cursor.moveToPosition(position);
            Circle circle = cursorCreator.create(cursor);
            return CircleDetailFragment.factory(circle).create();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }
    }

}