package circlebinder.common.card;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import circlebinder.common.checklist.ChecklistColor;
import circlebinder.common.event.Circle;
import circlebinder.common.search.CircleCursorConverter;
import circlebinder.common.search.CircleSearchOption;
import circlebinder.common.search.CircleSearchOptionBuilder;
import circlebinder.common.table.SQLite;

final class ChecklistCardCallable implements Callable<List<HomeCard>> {

    private final Context context;

    ChecklistCardCallable(Context context) {
        this.context = context;
    }

    @Override
    public List<HomeCard> call() throws Exception {
        List<HomeCard> cardList = new CopyOnWriteArrayList<>();
        CircleCursorConverter converter = new CircleCursorConverter(context);
        for (ChecklistColor checklistColor : ChecklistColor.checklists()) {
            CircleSearchOption option = new CircleSearchOptionBuilder()
                    .setChecklist(checklistColor)
                    .build();
            Cursor cursor = new SQLite(context).find(option);
            Log.e("ChecklisCard", "curso " + cursor.getCount());
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                Circle circle = converter.create(cursor);
                cardList.add(new ChecklistCard(circle));
            }
            cursor.close();
        }

        return cardList;
    }
}
