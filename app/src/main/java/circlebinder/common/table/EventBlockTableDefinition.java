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

public final class EventBlockTableDefinition implements TableDefinition {

    enum Field implements TableField {
        ID("_id", TableFieldType.INTEGER, Arrays.asList(FieldAttribute.PRIMARY_KEY)),
        BLOCK_TYPE_ID("block_type_id", TableFieldType.INTEGER, FieldAttribute.NONE()),
        BLOCK_NAME("block_name", TableFieldType.TEXT, FieldAttribute.NONE()),
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
                .unique(Field.BLOCK_NAME)
                .build();
    }

    @Override
    public Table getTable(SQLiteDatabase database) {
        return new EventBlockTable(database);
    }

    @Override
    public String getTableName() {
        return "event_blocks";
    }

    @Override
    public int getCreatedVersion() {
        return 4;
    }

}

