package circlebinder.common.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import net.ichigotake.common.database.CursorSimple;
import net.ichigotake.common.util.Optional;
import net.ichigotake.sqlitehelper.dml.Order;
import net.ichigotake.sqlitehelper.dml.Select;
import net.ichigotake.sqlitehelper.dml.Where;
import net.ichigotake.sqlitehelper.table.InsertableTable;
import net.ichigotake.sqlitehelper.table.Table;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import circlebinder.common.event.Block;
import circlebinder.common.event.BlockBuilder;
import circlebinder.common.event.EventBlockType;

public class EventBlockTable implements Table, InsertableTable<EventBlockTableForInsert> {

    private final SQLiteDatabase database;

    public EventBlockTable(SQLiteDatabase database) {
        this.database = database;
    }

    public List<Block> findAll() {
        List<Block> blocks = new CopyOnWriteArrayList<>();
        Cursor c = new Select(database, new EventBlockTableDefinition())
                .orderBy(new Order(EventBlockTableDefinition.Field.BLOCK_TYPE_ID, Order.Sequence.ASC))
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

    public Optional<Block> find(CharSequence name) {
        Cursor cursor = new Select(database, new EventBlockTableDefinition())
                .where(new Where(EventBlockTableDefinition.Field.BLOCK_NAME.getFieldName() + " = ?", name))
                .execute();
        if (!cursor.moveToNext()) {
            cursor.close();
            return Optional.empty();
        }
        Optional<Block> block = build(cursor);
        cursor.close();
        return block;
    }

    public Optional<Block> find(long id) {
        Cursor cursor = new Select(database, new EventBlockTableDefinition())
                .where(new Where(EventBlockTableDefinition.Field.ID.getFieldName() + " = ?", id))
                .execute();
        if (!cursor.moveToNext()) {
            cursor.close();
            return Optional.empty();
        }
        Optional<Block> block = build(cursor);
        cursor.close();
        return block;
    }

    @Override
    public void insertRow(EventBlockTableForInsert item) {
        ContentValues values = new ContentValues();
        values.put(EventBlockTableDefinition.Field.BLOCK_TYPE_ID.getFieldName(), item.getTypeId());
        values.put(EventBlockTableDefinition.Field.BLOCK_NAME.getFieldName(), item.getName());
        database.insert("event_blocks", null, values);
    }

    private Optional<Block> build(Cursor cursor) {
        CursorSimple c = new CursorSimple(cursor);
        String blockName = c.getString(EventBlockTableDefinition.Field.BLOCK_NAME.getFieldName());
        Long blockId = c.getLong(EventBlockTableDefinition.Field.ID.getFieldName());
        Integer blockTypeId = c.getInt(EventBlockTableDefinition.Field.BLOCK_TYPE_ID.getFieldName());

        assert !TextUtils.isEmpty(blockName);
        assert blockId >= 0;
        assert blockTypeId >= 0;

        BlockBuilder builder = new BlockBuilder()
                .setName(blockName)
                .setId(blockId)
                .setType(EventBlockType.get(blockTypeId));
        return Optional.of(builder.build());
    }

}
