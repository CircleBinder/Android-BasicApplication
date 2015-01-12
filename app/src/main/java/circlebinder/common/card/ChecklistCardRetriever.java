package circlebinder.common.card;

import android.content.Context;
import android.database.Cursor;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import circlebinder.common.checklist.ChecklistColor;
import circlebinder.common.event.Circle;
import circlebinder.common.search.CircleSearchOption;
import circlebinder.common.search.CircleSearchOptionBuilder;
import circlebinder.common.table.EventCircleTable;
import circlebinder.common.table.SQLite;

final class ChecklistCardRetriever implements Callable<List<HomeCard>> {

    private final Context context;

    ChecklistCardRetriever(Context context) {
        this.context = context;
    }

    @Override
    public List<HomeCard> call() throws Exception {
        List<HomeCard> cardList = new CopyOnWriteArrayList<>();
        for (ChecklistColor checklistColor : ChecklistColor.checklists()) {
            CircleSearchOption option = new CircleSearchOptionBuilder()
                    .setChecklist(checklistColor)
                    .build();
            Cursor cursor = EventCircleTable.find(SQLite.getDatabase(context), option);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (Circle circle : EventCircleTable.build(SQLite.getDatabase(context), cursor).asSet()) {
                    cardList.add(new ChecklistCard(circle));
                }
            }
            cursor.close();
        }

        return cardList;
    }
}
