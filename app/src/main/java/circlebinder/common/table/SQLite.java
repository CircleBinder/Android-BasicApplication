package circlebinder.common.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import net.ichigotake.common.database.CursorSimple;
import net.ichigotake.common.util.Optional;
import net.ichigotake.sqlitehelper.SQLiteOpenHelper;
import net.ichigotake.sqlitehelper.dml.Select;
import net.ichigotake.sqlitehelper.dml.Where;

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

public class SQLite {

    public static synchronized SQLiteDatabase getReadableDatabase(Context context) {
        return new SQLiteOpenHelper(context, new SQLiteConfiguration(context)).getReadableDatabase();
    }

    public static synchronized SQLiteDatabase getWritableDatabase(Context context) {
        return new SQLiteOpenHelper(context, new SQLiteConfiguration(context)).getWritableDatabase();
    }
    
    private final Context context;
    
    public SQLite(Context context) {
        this.context = context;
    }

    private synchronized SQLiteDatabase getReadableDatabase() {
        return new SQLiteOpenHelper(context, new SQLiteConfiguration(context)).getReadableDatabase();
    }

    private synchronized SQLiteDatabase getWritableDatabase() {
        return new SQLiteOpenHelper(context, new SQLiteConfiguration(context)).getWritableDatabase();
    }

    public void insert(EventCircleTableForInsert circle) {
        ContentValues values = new ContentValues();
        values.put(EventCircleTable.Field.BLOCK_ID.getFieldName(), circle.getBlockId());
        values.put(EventCircleTable.Field.CHECKLIST_ID.getFieldName(), circle.getChecklistId());
        values.put(EventCircleTable.Field.CIRCLE_NAME.getFieldName(), circle.getCircleName());
        values.put(EventCircleTable.Field.PEN_NAME.getFieldName(), circle.getPenName());
        values.put(EventCircleTable.Field.SPACE_NO.getFieldName(), circle.getSpaceNo());
        values.put(EventCircleTable.Field.SPACE_NO_SUB.getFieldName(), circle.getSpaceNoSub());
        values.put(EventCircleTable.Field.HOMEPAGE.getFieldName(), circle.getHomepage());
        getWritableDatabase().insert(new EventCircleTable().getTableName(), null, values);
    }

    public Optional<Circle> find(long circleId) {
        Cursor cursor = new Select(getReadableDatabase(), new EventCircleTable())
                .where(new Where(EventCircleTable.Field.ID.getFieldName(), circleId))
                .execute();
        Optional<Circle> circle = build(cursor);
        //TODO
        cursor.close();
        return circle;
    }

    public Optional<Circle> build(Cursor cursor) {
        CursorSimple c = new CursorSimple(cursor);
        Optional<Block> block = new EventBlockTable(context).get(c.getLong(EventCircleTable.Field.BLOCK_ID.getFieldName()));
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
        ChecklistColor checklist = ChecklistColor.getById(c.getInt(EventCircleTable.Field.CHECKLIST_ID.getFieldName()));
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

    public Optional<Circle> findOne(CircleSearchOption searchOption) {
        Cursor cursor = find(searchOption);
        Optional<Circle> circle = build(cursor);
        //TODO
//        cursor.close();
        return circle;
    }

    public Cursor find(CircleSearchOption searchOption) {
        Select query = new Select(getReadableDatabase(), new EventCircleTable());
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
                where.and(EventCircleTable.Field.CHECKLIST_ID + " = ?", searchOption.getChecklist().getId());
            } else {
                where.and(EventCircleTable.Field.CHECKLIST_ID + " > ?", 0);
            }
        }
        query.where(where);
//        query.orderBy(new Order(EventCircleTable.Field.ID));
        return query.execute();
    }

    public void setChecklist(Circle circle, ChecklistColor color) {
        EventCircleTableForInsert.Builder builder = new EventCircleTableForInsert.Builder(circle);
        builder.setChecklistId(color.getId());
        SQLiteDatabase database = SQLite.getWritableDatabase(context);
        insert(builder.build());
    }

}
