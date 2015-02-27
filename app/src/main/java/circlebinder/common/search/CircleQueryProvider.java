package circlebinder.common.search;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.FilterQueryProvider;

import circlebinder.common.table.EventCircleTable;

public final class CircleQueryProvider implements FilterQueryProvider {

    private final EventCircleTable eventCircleTable;
    private final CircleSearchOption circleSearchOption;

    public CircleQueryProvider(SQLiteDatabase database, CircleSearchOption circleSearchOption) {
        this.eventCircleTable = new EventCircleTable(database);
        this.circleSearchOption = circleSearchOption;
    }

    @Override
    public Cursor runQuery(CharSequence constraint) {
        return eventCircleTable.find(circleSearchOption);
    }

}
