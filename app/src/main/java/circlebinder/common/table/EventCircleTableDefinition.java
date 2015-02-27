package circlebinder.common.table;

import android.database.sqlite.SQLiteDatabase;

import net.ichigotake.sqlitehelper.schema.FieldAttribute;
import net.ichigotake.sqlitehelper.schema.TableDefinition;
import net.ichigotake.sqlitehelper.schema.TableField;
import net.ichigotake.sqlitehelper.schema.TableFieldType;
import net.ichigotake.sqlitehelper.schema.TableSchema;
import net.ichigotake.sqlitehelper.schema.TableSchemaBuilder;
import net.ichigotake.sqlitehelper.table.Table;

import java.util.Arrays;
import java.util.List;

public final class EventCircleTableDefinition implements TableDefinition {

    enum Field implements TableField {
        ID("_id", TableFieldType.INTEGER, Arrays.asList(FieldAttribute.PRIMARY_KEY)),
        BLOCK_ID("block_id", TableFieldType.LONG, FieldAttribute.NONE()),
        SPACE_NO("space_no", TableFieldType.INTEGER, FieldAttribute.NONE()),
        SPACE_NO_SUB("space_no_sub", TableFieldType.INTEGER, FieldAttribute.NONE()),
        CIRCLE_NAME("circle_name", TableFieldType.TEXT, FieldAttribute.NONE()),
        PEN_NAME("pen_name", TableFieldType.TEXT, FieldAttribute.NONE()),
        HOMEPAGE("homepage", TableFieldType.TEXT, FieldAttribute.NONE()),
        CHECKLIST_ID("checklist_id", TableFieldType.LONG, FieldAttribute.NONE()),
        ;

        private final String name;
        private final TableFieldType type;
        private final List<FieldAttribute> attributes;

        private Field(String name, TableFieldType type, List<FieldAttribute> attributes) {
            this.name = name;
            this.type = type;
            this.attributes = attributes;
        }

        @Override
        public List<FieldAttribute> getAttributes() {
            return attributes;
        }

        @Override
        public String getFieldName() {
            return name;
        }

        @Override
        public TableFieldType getFieldType() {
            return type;
        }
    }

    @Override
    public TableSchema getTableSchema() {
        return new TableSchemaBuilder(getTableName())
                .field(Field.values())
                .build();
    }

    @Override
    public Table getTable(SQLiteDatabase database) {
        return new EventCircleTable(database);
    }

    @Override
    public String getTableName() {
        return "event_circles";
    }

    @Override
    public int getCreatedVersion() {
        return 12;
    }

}

