package circlebinder.common.table;

import net.ichigotake.sqlitehelper.Configuration;
import net.ichigotake.sqlitehelper.MigrationCallback;
import net.ichigotake.sqlitehelper.NoMigrationCallback;
import net.ichigotake.sqlitehelper.schema.TableDefinition;

import java.util.Arrays;
import java.util.List;

public class SQLiteConfiguration implements Configuration {
    
    @Override
    public List<TableDefinition> getDatabaseTables() {
        return Arrays.asList(
                new EventBlockTableDefinition(),
                new EventCircleTableDefinition()
//                TBD
//                new EventChecklistHistoryTable(),
//                new EventTable()
        );
    }

    @Override
    public int getDatabaseVersion() {
        return 14;
    }

    @Override
    public String getDatabaseName() {
        return "CircleBinder.db";
    }

    @Override
    public MigrationCallback getMigrationCallback() {
        return new NoMigrationCallback();
    }
}
