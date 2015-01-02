package circlebinder.common.table;

import net.ichigotake.sqlitehelper.schema.Table;
import net.ichigotake.sqlitehelper.schema.TableField;
import net.ichigotake.sqlitehelper.schema.TableSchema;

import java.util.List;

public final class EventTable implements Table {

    public static final String NAME = "events";
    public static final String FIELD_ID = "_id";
    public static final String FIELD_EVENT_ID = "event_id";
    public static final String FIELD_EVENT_NUMBER = "event_number";
    public static final String FIELD_EVENT_NAME = "event_name";

    EventTable() {
        throw new IllegalStateException("TBD");
    }

    @Override
    public int getSenseVersion() {
        return 0;
    }

    @Override
    public TableSchema getTableSchema() {
        return null;
    }

    @Override
    public List<TableField> getTableFields() {
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }

}


