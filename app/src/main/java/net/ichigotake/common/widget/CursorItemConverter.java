package net.ichigotake.common.widget;

import android.database.Cursor;

import net.ichigotake.common.util.Optional;

public interface CursorItemConverter<ITEM> {

    Optional<ITEM> create(Cursor cursor);
}
