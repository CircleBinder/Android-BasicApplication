package circlebinder.common.table;

import android.content.Context;
import android.database.Cursor;

import net.ichigotake.common.content.AsyncTaskLoader;

import circlebinder.common.search.CircleSearchOption;

public final class EventCircleLoader extends AsyncTaskLoader<Cursor> {

    private final CircleSearchOption searchOption;

    public EventCircleLoader(Context context, CircleSearchOption searchOption) {
        super(context);
        this.searchOption = searchOption;
    }

    @Override
    public Cursor loadInBackground() {
        return new SQLite(getContext()).find(searchOption);
    }

}
