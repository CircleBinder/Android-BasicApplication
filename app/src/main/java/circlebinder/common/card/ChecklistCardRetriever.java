package circlebinder.common.card;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import circlebinder.common.checklist.ChecklistColor;
import circlebinder.common.event.Circle;
import circlebinder.common.search.CircleSearchOption;
import circlebinder.common.search.CircleSearchOptionBuilder;
import circlebinder.common.table.EventCircleTable;

final class ChecklistCardRetriever implements Callable<List<HomeCard>> {

    private final EventCircleTable eventCircleTable;

    ChecklistCardRetriever(SQLiteDatabase database) {
        this.eventCircleTable = new EventCircleTable(database);
    }

    @Override
    public List<HomeCard> call() throws Exception {
        List<HomeCard> cardList = new CopyOnWriteArrayList<>();
        for (ChecklistColor checklistColor : ChecklistColor.checklists()) {
            CircleSearchOption option = new CircleSearchOptionBuilder()
                    .setChecklist(checklistColor)
                    .build();
            Cursor cursor = eventCircleTable.find(option);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (Circle circle : eventCircleTable.build(cursor).asSet()) {
                    cardList.add(new ChecklistCard(circle));
                }
            }
            cursor.close();
        }

        return cardList;
    }
}
