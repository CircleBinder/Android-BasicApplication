package circlebinder.common.table;

import android.content.Context;

import net.ichigotake.sqlitehelper.Configuration;
import net.ichigotake.sqlitehelper.schema.Table;

import java.util.Arrays;
import java.util.List;

public class SQLiteConfiguration implements Configuration {
    
    private final Context context;

    public SQLiteConfiguration(Context context) {
        this.context = context;
    }

    @Override
    public List<Table> getDatabaseTables() {
        return Arrays.asList(
                new EventBlockTable(context),
                new EventCircleTable()
//                new EventChecklistHistoryTable(),
//                new EventTable()
        );
    }

    @Override
    public int getDatabaseVersion() {
        return 13;
    }

    @Override
    public String getDatabaseName() {
        return "CircleBinder.db";
    }
}
