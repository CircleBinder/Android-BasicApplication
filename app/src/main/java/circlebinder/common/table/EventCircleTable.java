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
import net.ichigotake.sqlitehelper.table.InsertableTable;
import net.ichigotake.sqlitehelper.table.Table;
import net.ichigotake.sqlitehelper.table.UpdatableTable;

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

public final class EventCircleTable
        implements Table, InsertableTable<EventCircleTableForInsert>, UpdatableTable<EventCircleTableForUpdate> {

    private final SQLiteDatabase database;
    private final EventCircleTableDefinition definition;
    private final EventBlockTable eventBlockTable;

    public EventCircleTable(SQLiteDatabase database) {
        this.database = database;
        this.definition = new EventCircleTableDefinition();
        this.eventBlockTable = new EventBlockTable(database);
    }

    @Override
    public void insertRow(EventCircleTableForInsert circle) {
        ContentValues values = new ContentValues();
        values.put(EventCircleTableDefinition.Field.BLOCK_ID.getFieldName(), circle.getBlockId());
        values.put(EventCircleTableDefinition.Field.CHECKLIST_ID.getFieldName(), circle.getChecklistId());
        values.put(EventCircleTableDefinition.Field.CIRCLE_NAME.getFieldName(), circle.getCircleName());
        values.put(EventCircleTableDefinition.Field.PEN_NAME.getFieldName(), circle.getPenName());
        values.put(EventCircleTableDefinition.Field.SPACE_NO.getFieldName(), circle.getSpaceNo());
        values.put(EventCircleTableDefinition.Field.SPACE_NO_SUB.getFieldName(), circle.getSpaceNoSub());
        values.put(EventCircleTableDefinition.Field.HOMEPAGE.getFieldName(), circle.getHomepage());
        database.insert(definition.getTableName(), null, values);
    }

    public Optional<Circle> build(Cursor cursor) {
        CursorSimple c = new CursorSimple(cursor);
        Optional<Block> block = eventBlockTable
                .find(c.getLong(EventCircleTableDefinition.Field.BLOCK_ID.getFieldName()));
        assert block.isPresent();

        int spaceNo = c.getInt(EventCircleTableDefinition.Field.SPACE_NO.getFieldName());
        String spaceNoSub = Space
                .parseNoSub(c.getInt(EventCircleTableDefinition.Field.SPACE_NO_SUB.getFieldName()));
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
        String name = c.getString(EventCircleTableDefinition.Field.CIRCLE_NAME.getFieldName());
        long circleId = c.getLong(EventCircleTableDefinition.Field.ID.getFieldName());
        ChecklistColor checklist = ChecklistColor
                .getById(c.getInt(EventCircleTableDefinition.Field.CHECKLIST_ID.getFieldName()));
        List<CircleLink> linkList = new CopyOnWriteArrayList<>();
        String homepageUrl = c.getString(EventCircleTableDefinition.Field.HOMEPAGE.getFieldName());
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
                .setPenName(c.getString(EventCircleTableDefinition.Field.PEN_NAME.getFieldName()))
                .setName(name)
                .setSpace(spaceBuilder.build())
                .setChecklistColor(checklist)
                .setGenre(new GenreBuilder().setName("").build())
                .setLink(new CircleLinks(linkList))
                .build());
    }

    public Cursor selectAll() {
        return find(new CircleSearchOptionBuilder().build());
    }

    public Optional<Circle> findOne(CircleSearchOption searchOption) {
        Cursor cursor = find(searchOption);
        if (!cursor.moveToNext()) {
            cursor.close();
            return Optional.empty();
        }
        Optional<Circle> circle = build(cursor);
        cursor.close();
        return circle;
    }

    public Cursor find(CircleSearchOption searchOption) {
        Select query = new Select(database, definition);
        Where where = new Where();
        if (searchOption.hasKeyword()) {
            where.and(
                    new Where(EventCircleTableDefinition.Field.CIRCLE_NAME.getFieldName() + " LIKE ?", "%" + searchOption.getKeyword() + "%")
                            .or(EventCircleTableDefinition.Field.PEN_NAME.getFieldName() + " LIKE ?", "%" + searchOption.getKeyword() + "%")
            );
        }
        if (searchOption.hasBlock() && searchOption.getBlock().getId() > 0) {
            where.and(EventCircleTableDefinition.Field.BLOCK_ID.getFieldName() + " = ?", searchOption.getBlock().getId());
        }
        if (searchOption.hasChecklist()) {
            if (ChecklistColor.isChecklist(searchOption.getChecklist())) {
                where.and(EventCircleTableDefinition.Field.CHECKLIST_ID.getFieldName() + " = ?", searchOption.getChecklist().getId());
            } else {
                where.and(EventCircleTableDefinition.Field.CHECKLIST_ID.getFieldName() + " > ?", 0);
            }
        }
        query.where(where);
        return query.execute();
    }

    @Override
    public void updateItem(EventCircleTableForUpdate item) {
        Where where = new Where(EventCircleTableDefinition.Field.ID.getFieldName() + " = ?", item.getId());
        ContentValues values = new ContentValues();
        values.put(EventCircleTableDefinition.Field.CHECKLIST_ID.getFieldName(), item.getChecklistId());
        database.update(definition.getTableName(), values, where.getQuery(), where.getArguments());
    }

}
