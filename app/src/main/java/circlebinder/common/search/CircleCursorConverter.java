package circlebinder.common.search;

import android.content.Context;
import android.database.Cursor;

import net.ichigotake.common.widget.CursorItemConverter;

import circlebinder.common.Legacy;
import circlebinder.common.event.Circle;
import circlebinder.common.table.SQLite;

public final class CircleCursorConverter implements CursorItemConverter<Circle>, Legacy {

    private final Context context;

    public CircleCursorConverter(Context context) {
        this.context = context;
    }

    @Override
    public Circle create(Cursor cursor) {
        return new SQLite(context).build(cursor).or(null);
    }
}
