package circlebinder.creation.initialize;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteTransactionListener;
import android.text.TextUtils;

import net.ichigotake.common.util.Optional;
import net.ichigotake.sqlitehelper.SQLiteTransaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import am.ik.ltsv4j.LTSV;
import circlebinder.common.Legacy;
import circlebinder.common.event.Block;
import circlebinder.common.event.CircleBuilder;
import circlebinder.common.event.Space;
import circlebinder.R;
import circlebinder.common.table.EventBlockTable;
import circlebinder.common.event.EventBlockType;
import circlebinder.common.table.EventBlockTableForInsert;
import circlebinder.common.table.EventCircleTableForInsert;
import circlebinder.common.table.SQLite;

abstract class CreationDatabaseInitialize implements Runnable, Legacy {

    private final Context context;
    private final CircleBuilder builder;
    private final Resources resources;

    CreationDatabaseInitialize(Context context) {
        this.context = context;
        this.resources = context.getResources();
        this.builder = new CircleBuilder();
    }

    abstract void finished();

    @Override
    public void run() {
        new SQLiteTransaction(SQLite.getWritableDatabase(context)).execute(new SQLiteTransactionListener() {
            @Override
            public void onBegin() {
                try {
                    initBlock();
                    initCircle();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCommit() {

            }

            @Override
            public void onRollback() {

            }
        });
        new LegacyAppStorage(context).setInitialized(true);
        finished();
    }

    private void initBlock() throws IOException {
        InputStream inputStream = resources.openRawResource(R.raw.creation_spaces);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            SQLiteDatabase database = SQLite.getWritableDatabase(context);
            EventBlockTable table = new EventBlockTable(context);
            while ((line = reader.readLine()) != null) {
                Map<String, String> space = LTSV.parser().parseLine(line);
                if (TextUtils.isEmpty(space.get("block"))) {
                    continue;
                }

                EventBlockTableForInsert block = new EventBlockTableForInsert.Builder()
                        .setName(space.get("block"))
                        .setType(EventBlockType.一般的なスタイル)
                        .build();
                table.insert(database, block);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void initCircle() throws IOException {
        InputStream inputStream = resources.openRawResource(R.raw.creation_circles);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            CreationSpaceFactory creationSpaceFactory = new CreationSpaceFactory();
            EventBlockTable blockTable = new EventBlockTable(context);
            while ((line = reader.readLine()) != null) {
                Map<String, String> circle = LTSV.parser().parseLine(line);
                String circleName = circle.get("circle_name");
                if (TextUtils.isEmpty(circleName)) {
                    continue;
                }

                builder.clear();

                Space space = creationSpaceFactory.from(circle.get("space"));

                Optional<Block> block = blockTable.get(space.getBlockName());
                assert !block.isPresent();
                EventCircleTableForInsert insertCircle = new EventCircleTableForInsert.Builder()
                        .setBlockId(block.get().getId())
                        .setChecklistId(0)
                        .setCircleName(circleName)
                        .setPenName(circle.get("pen_name"))
                        .setHomepage(circle.get("circle_url"))
                        .setSpaceNo(space.getNo())
                        .setSpaceNoSub("a".equals(space.getNoSub()) ? 0 : 1)
                        .build();
                new SQLite(context).insert(insertCircle);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

}
