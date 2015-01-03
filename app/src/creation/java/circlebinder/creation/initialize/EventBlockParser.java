package circlebinder.creation.initialize;

import android.text.TextUtils;

import net.ichigotake.common.util.Optional;

import java.util.Map;

import circlebinder.common.event.EventBlockType;
import circlebinder.common.table.EventBlockTableForInsert;

class EventBlockParser implements LTSVParser<EventBlockTableForInsert> {
    
    @Override
    public Optional<EventBlockTableForInsert> parseLTSV(Map<String, String> line) {
        if (TextUtils.isEmpty(line.get("block"))) {
            return Optional.empty();
        }
        return Optional.of(new EventBlockTableForInsert.Builder()
                .setName(line.get("block"))
                .setType(EventBlockType.一般的なスタイル)
                .build());
    }
    
}
