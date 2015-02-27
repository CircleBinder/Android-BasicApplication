package circlebinder.common.search;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.ichigotake.common.util.Optional;
import net.ichigotake.common.widget.CursorItemConverter;

import circlebinder.common.Legacy;
import circlebinder.common.event.Circle;
import circlebinder.common.table.EventCircleTable;

public final class CircleCursorConverter implements CursorItemConverter<Circle>, Legacy {

    private final EventCircleTable eventCircleTable;

    public CircleCursorConverter(SQLiteDatabase database) {
        this.eventCircleTable = new EventCircleTable(database);
    }

    @Override
    public Optional<Circle> create(Cursor cursor) {
        return eventCircleTable.build(cursor);
    }
}
