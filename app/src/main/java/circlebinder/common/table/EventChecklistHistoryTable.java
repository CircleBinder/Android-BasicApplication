package circlebinder.common.table;

import net.ichigotake.sqlitehelper.schema.Table;
import net.ichigotake.sqlitehelper.schema.TableField;
import net.ichigotake.sqlitehelper.schema.TableSchema;

import java.util.List;

// TODO: TBD
public final class EventChecklistHistoryTable implements Table {

    public static final String NAME = "event_checklist_history";
    public static final String FIELD_ID = "_id";
    public static final String FIELD_EVENT_ID = EventTable.FIELD_EVENT_ID;
    public static final String FIELD_EVENT_NAME = "event_name";
    public static final String FIELD_EVENT_NUMBER = "event_number";
    public static final String FIELD_SPACE_NAME = "space_name";
    public static final String FIELD_CIRCLE_NAME =" circle_name";
    public static final String FIELD_PEN_NAME = "pen_name";
    public static final String FIELD_HOMEPAGE = "homepage";
    public static final String FIELD_CHECKLIST_ID = "checklist_id";
    public static final String FIELD_CHECKLIST_NAME = "checklist_name";

    public EventChecklistHistoryTable() {
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
        return NAME;
    }

}

