package circlebinder.common.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import net.ichigotake.common.database.CursorSimple;
import net.ichigotake.common.util.Optional;
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

import circlebinder.R;
import circlebinder.common.app.CircleBinderApplication;
import circlebinder.common.checklist.ChecklistColor;
import circlebinder.common.event.Block;
import circlebinder.common.event.Circle;
import circlebinder.common.event.CircleBuilder;
import circlebinder.common.event.CircleLink;
import circlebinder.common.event.CircleLinkBuilder;
import circlebinder.common.event.CircleLinkType;
import circlebinder.common.event.CircleLinks;
import circlebinder.common.event.GenreBuilder;
import circlebinder.common.event.Space;
import circlebinder.common.event.SpaceBuilder;
import circlebinder.common.search.CircleSearchOption;
import circlebinder.common.search.CircleSearchOptionBuilder;

public final class EventCircleTable implements Table {

    public static long insert(SQLiteDatabase database, EventCircleTableForInsert circle) {
        ContentValues values = new ContentValues();
        values.put(EventCircleTable.Field.BLOCK_ID.getFieldName(), circle.getBlockId());
        values.put(EventCircleTable.Field.CHECKLIST_ID.getFieldName(), circle.getChecklistId());
        values.put(EventCircleTable.Field.CIRCLE_NAME.getFieldName(), circle.getCircleName());
        values.put(EventCircleTable.Field.PEN_NAME.getFieldName(), circle.getPenName());
        values.put(EventCircleTable.Field.SPACE_NO.getFieldName(), circle.getSpaceNo());
        values.put(EventCircleTable.Field.SPACE_NO_SUB.getFieldName(), circle.getSpaceNoSub());
        values.put(EventCircleTable.Field.HOMEPAGE.getFieldName(), circle.getHomepage());
        return database.insert(new EventCircleTable().getTableName(), null, values);
    }

    public static Optional<Circle> build(SQLiteDatabase database, Cursor cursor) {
        CursorSimple c = new CursorSimple(cursor);
        Optional<Block> block = new EventBlockTable()
                .get(database, c.getLong(EventCircleTable.Field.BLOCK_ID.getFieldName()));
        assert block.isPresent();

        int spaceNo = c.getInt(EventCircleTable.Field.SPACE_NO.getFieldName());
        String spaceNoSub = Space.parseNoSub(c.getInt(EventCircleTable.Field.SPACE_NO_SUB.getFieldName()));
        String spaceSimpleName = String.format(CircleBinderApplication.APP_LOCALE,
                "%s%02d%s", block.get().getName(), spaceNo, spaceNoSub
        );
        String spaceName = String.format(CircleBinderApplication.APP_LOCALE,
                "%s-%02d%s", block.get().getName(), spaceNo, spaceNoSub
        );
        SpaceBuilder spaceBuilder = new SpaceBuilder()
                .setName(spaceName)
                .setSimpleName(spaceSimpleName)
                .setBlockName(block.get().getName())
                .setNo(spaceNo)
                .setNoSub(spaceNoSub);
        String name = c.getString(EventCircleTable.Field.CIRCLE_NAME.getFieldName());
        long circleId = c.getLong(EventCircleTable.Field.ID.getFieldName());
        ChecklistColor checklist = ChecklistColor
                .getById(c.getInt(EventCircleTable.Field.CHECKLIST_ID.getFieldName()));
        List<CircleLink> linkList = new CopyOnWriteArrayList<>();
        String homepageUrl = c.getString(EventCircleTable.Field.HOMEPAGE.getFieldName());
        if (!TextUtils.isEmpty(homepageUrl)) {
            CircleLink link = new CircleLinkBuilder()
                    .setIcon(R.drawable.ic_action_attach)
                    .setUri(Uri.parse(homepageUrl))
                    .setType(CircleLinkType.HOMEPAGE)
                    .build();
            linkList.add(link);
        }

        return Optional.of(new CircleBuilder()
                .setId(circleId)
                .setPenName(c.getString(EventCircleTable.Field.PEN_NAME.getFieldName()))
                .setName(name)
                .setSpace(spaceBuilder.build())
                .setChecklistColor(checklist)
                .setGenre(new GenreBuilder().setName("").build())
                .setLink(new CircleLinks(linkList))
                .build());
    }

    public static Cursor findAll(SQLiteDatabase database) {
        return find(database, new CircleSearchOptionBuilder().build());
    }

    public static Optional<Circle> findOne(SQLiteDatabase database, CircleSearchOption searchOption) {
        Cursor cursor = find(database, searchOption);
        if (!cursor.moveToNext()) {
            cursor.close();
            return Optional.empty();
        }
        Optional<Circle> circle = build(database, cursor);
        cursor.close();
        return circle;
    }

    public static Cursor find(SQLiteDatabase database, CircleSearchOption searchOption) {
        Select query = new Select(database, new EventCircleTable());
        Where where = new Where();
        if (searchOption.hasKeyword()) {
            where.and(
                    new Where(EventCircleTable.Field.CIRCLE_NAME.getFieldName() + " LIKE ?", "%" + searchOption.getKeyword() + "%")
                            .or(EventCircleTable.Field.PEN_NAME.getFieldName() + " LIKE ?", "%" + searchOption.getKeyword() + "%")
            );
        }
        if (searchOption.hasBlock() && searchOption.getBlock().getId() > 0) {
            where.and(EventCircleTable.Field.BLOCK_ID.getFieldName() + " = ?", searchOption.getBlock().getId());
        }
        if (searchOption.hasChecklist()) {
            if (ChecklistColor.isChecklist(searchOption.getChecklist())) {
                where.and(EventCircleTable.Field.CHECKLIST_ID.getFieldName() + " = ?", searchOption.getChecklist().getId());
            } else {
                where.and(EventCircleTable.Field.CHECKLIST_ID.getFieldName() + " > ?", 0);
            }
        }
        query.where(where);
        return query.execute();
    }

    public static void setChecklist(SQLiteDatabase database, Circle circle, ChecklistColor color) {
        EventCircleTableForInsert.Builder builder = new EventCircleTableForInsert.Builder(circle);
        builder.setChecklistId(color.getId());
        Where where = new Where(Field.ID.getFieldName() + " = ?", circle.getId());
        ContentValues values = new ContentValues();
        values.put(Field.CHECKLIST_ID.getFieldName(), color.getId());
        database.update(NAME, values, where.getQuery(), where.getArguments());
    }

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

    public static final String NAME = "event_circles";

    @Override
    public int getSenseVersion() {
        return 12;
    }

    @Override
    public TableSchema getTableSchema() {
        return new TableSchemaBuilder("event_circles")
                .field(Field.values())
                .build();
    }

    @Override
    public List<TableField> getTableFields() {
        return Arrays.<TableField>asList(Field.values());
    }

    @Override
    public String getTableName() {
        return NAME;
    }

}
