package circlebinder.creation.initialize;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import net.ichigotake.common.util.Optional;

import java.util.Map;

import circlebinder.common.event.Block;
import circlebinder.common.event.Space;
import circlebinder.common.table.EventBlockTable;
import circlebinder.common.table.EventCircleTableForInsert;

class EventCircleParser implements LTSVParser<EventCircleTableForInsert> {

    private final EventBlockTable blockTable;
    private final CreationSpaceFactory creationSpaceFactory;
    private final SQLiteDatabase database;

    EventCircleParser(Context context, SQLiteDatabase database) {
        this.blockTable = new EventBlockTable(context);
        this.creationSpaceFactory = new CreationSpaceFactory();
        this.database = database;
    }
    
    @Override
    public Optional<EventCircleTableForInsert> parseLTSV(Map<String, String> line) {
                String circleName = line.get("circle_name");
                if (TextUtils.isEmpty(circleName)) {
                    return Optional.empty();
                }
                Space space = creationSpaceFactory.from(line.get("space"));
                Optional<Block> block = blockTable.get(database, space.getBlockName());
                assert block.isPresent();
                return Optional.of(new EventCircleTableForInsert.Builder()
                        .setBlockId(block.get().getId())
                        .setChecklistId(0)
                        .setCircleName(circleName)
                        .setPenName(line.get("pen_name"))
                        .setHomepage(line.get("circle_url"))
                        .setSpaceNo(space.getNo())
                        .setSpaceNoSub("a".equals(space.getNoSub()) ? 0 : 1)
                        .build());
    }

}
