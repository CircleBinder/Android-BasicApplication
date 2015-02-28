package circlebinder.creation.home;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import net.ichigotake.common.app.ActivityTripper;
import net.ichigotake.common.util.Finders;
import net.ichigotake.common.util.ViewFinder;

import circlebinder.R;
import circlebinder.common.app.phone.CircleSearchActivity;
import circlebinder.common.card.HomeCard;
import circlebinder.common.card.HomeCardAdapter;

public final class HomeCardListView extends FrameLayout {

    private final HomeCardClickEventHandler navigator;
    private GridView gridView;
    
    @SuppressWarnings("unused") // Public API
    public HomeCardListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.navigator = new HomeCardClickEventHandler();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewFinder finder = Finders.from(
                inflater.inflate(R.layout.creation_view_checklist_list, this, true)
        );
        View headerView = finder.findOrNull(R.id.creation_view_checklist_list_header);
        headerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                navigator.headerClicked(getContext());
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            headerView.setBackgroundResource(R.drawable.common_ripple);
            ViewCompat.setElevation(headerView, 10);
        }
        gridView = finder.findOrNull(R.id.creation_view_checklist_list);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                navigator.itemClicked(getContext(), (HomeCard) parent.getItemAtPosition(position));
            }
        });
    }
    
    public void setAdapter(HomeCardAdapter adapter) {
        gridView.setAdapter(adapter);
    }

    private static class HomeCardClickEventHandler {

        void headerClicked(Context context) {
            ActivityTripper
                    .from(context, CircleSearchActivity.with())
                    .trip();
        }

        void itemClicked(Context context, HomeCard item) {
            ActivityTripper.from(context, item.createTransitIntentFactory(context))
                    .trip();
        }

    }

}
