package circlebinder.common.search;

import android.content.Context;
import android.database.Cursor;
import android.widget.FilterQueryProvider;

import circlebinder.common.table.EventCircleTable;
import circlebinder.common.table.SQLite;

public final class CircleQueryProvider implements FilterQueryProvider {

    private final Context context;
    private final CircleSearchOption circleSearchOption;

    public CircleQueryProvider(Context context, CircleSearchOption circleSearchOption) {
        this.context = context;
        this.circleSearchOption = circleSearchOption;
    }

    @Override
    public Cursor runQuery(CharSequence constraint) {
        return EventCircleTable.find(SQLite.getDatabase(context), circleSearchOption);
    }

}
