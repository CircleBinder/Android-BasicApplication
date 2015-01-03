package circlebinder.common.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import net.ichigotake.common.database.CursorSimple;
import net.ichigotake.common.util.Optional;
import net.ichigotake.sqlitehelper.dml.Order;
import net.ichigotake.sqlitehelper.dml.Select;
import net.ichigotake.sqlitehelper.dml.Where;
import net.ichigotake.sqlitehelper.schema.FieldAttribute;
import net.ichigotake.sqlitehelper.schema.Table;
import net.ichigotake.sqlitehelper.schema.TableField;
import net.ichigotake.sqlitehelper.schema.TableFieldType;
import net.ichigotake.sqlitehelper.schema.TableSchema;
import net.ichigotake.sqlitehelper.schema.TableSchemaBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import circlebinder.common.event.Block;
import circlebinder.common.event.BlockBuilder;
import circlebinder.common.event.EventBlockType;

public final class EventBlockTable implements Table {

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
    
    private final Context context;

    public EventBlockTable(Context context) {
        this.context = context;
    }

    public void insert(SQLiteDatabase database, EventBlockTableForInsert block) {
        EventBlockTable table = new EventBlockTable(context);
        ContentValues values = new ContentValues();
        values.put(Field.BLOCK_TYPE_ID.getFieldName(), block.getTypeId());
        values.put(Field.BLOCK_NAME.getFieldName(), block.getName());
        database.insert(table.getTableName(), null, values);
    }

    public List<Block> getAll() {
        List<Block> blocks = new CopyOnWriteArrayList<>();
        Cursor c = new Select(SQLite.getWritableDatabase(context), new EventBlockTable(context))
                .orderBy(new Order(Field.BLOCK_TYPE_ID, Order.Sequence.ASC))
                .execute();

        while (c.moveToNext()) {
            Optional<Block> item = build(c);
            assert item.isPresent();
            for (Block value : item.asSet()) {
                blocks.add(value);
            }
        }
        c.close();

        return blocks;
    }

    public Optional<Block> get(SQLiteDatabase database, CharSequence name) {
        Cursor cursor = new Select(database, new EventBlockTable(context))
                .where(new Where(Field.BLOCK_NAME.getFieldName() + " = ?", name))
                .execute();
        if (!cursor.moveToNext()) {
            cursor.close();
            return Optional.empty();
        }
        Optional<Block> block = build(cursor);
        cursor.close();
        return block;
    }

    public Optional<Block> get(long id) {
        Cursor cursor = new Select(SQLite.getWritableDatabase(context), new EventBlockTable(context))
                .where(new Where(Field.ID.getFieldName() + " = ?", id))
                .execute();
        if (!cursor.moveToNext()) {
            cursor.close();
            return Optional.empty();
        }
        Optional<Block> block = build(cursor);
        cursor.close();
        return block;
    }

    private Optional<Block> build(Cursor cursor) {
        CursorSimple c = new CursorSimple(cursor);
        String blockName = c.getString(Field.BLOCK_NAME.getFieldName());
        Long blockId = c.getLong(Field.ID.getFieldName());
        Integer blockTypeId = c.getInt(Field.BLOCK_TYPE_ID.getFieldName());

        assert !TextUtils.isEmpty(blockName);
        assert blockId >= 0;
        assert blockTypeId >= 0;

        BlockBuilder builder = new BlockBuilder()
                .setName(blockName)
                .setId(blockId)
                .setType(EventBlockType.get(blockTypeId));
        return Optional.of(builder.build());
    }

    @Override
    public int getSenseVersion() {
        return 4;
    }

    @Override
    public TableSchema getTableSchema() {
        return new TableSchemaBuilder(getTableName())
                .field(getTableFields())
                .unique(Field.BLOCK_NAME)
                .build();
    }

    @Override
    public List<TableField> getTableFields() {
        return Arrays.<TableField>asList(Field.values());
    }

    @Override
    public String getTableName() {
        return "event_blocks";
    }

}
