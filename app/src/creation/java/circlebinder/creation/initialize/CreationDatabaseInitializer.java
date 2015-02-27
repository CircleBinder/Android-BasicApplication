package circlebinder.creation.initialize;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.io.InputStream;

import circlebinder.common.event.CircleBuilder;
import circlebinder.R;
import circlebinder.common.table.EventBlockTable;
import circlebinder.common.table.EventBlockTableForInsert;
import circlebinder.common.table.EventCircleTable;
import circlebinder.common.table.EventCircleTableForInsert;

public class CreationDatabaseInitializer {

    private final Context context;
    private final CircleBuilder builder;
    private final Resources resources;

    public CreationDatabaseInitializer(Context context) {
        this.context = context;
        this.resources = context.getResources();
        this.builder = new CircleBuilder();
    }

    public void initialize(SQLiteDatabase database) {
        database.beginTransaction();
        try {
            initializeBlocks(database, resources.openRawResource(R.raw.creation_spaces_ltsv));
            initializeCircles(database, resources.openRawResource(R.raw.creation_circles_ltsv));
            database.setTransactionSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
        new LegacyAppStorage(context).setInitialized(true);
    }

    /* visible for testing */
    void initializeBlocks(final SQLiteDatabase database, InputStream inputStream) throws IOException {
        new LTSVReader<>(inputStream, new EventBlockParser(), new LTSVReadLineListener<EventBlockTableForInsert>() {
            @Override
            public void onLineRead(EventBlockTableForInsert item) {
                new EventBlockTable(database).insertRow(item);
            }
        }).read();
    }

    /* visible for testing */
    void initializeCircles(final SQLiteDatabase database, InputStream inputStream) throws IOException {
        new LTSVReader<>(inputStream, new EventCircleParser(database), new LTSVReadLineListener<EventCircleTableForInsert>() {
            @Override
            public void onLineRead(EventCircleTableForInsert item) {
                builder.clear();
                new EventCircleTable(database).insertRow(item);
            }
        }).read();
    }
    
}
