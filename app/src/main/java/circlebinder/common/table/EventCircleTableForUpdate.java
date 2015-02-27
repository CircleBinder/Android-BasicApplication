package circlebinder.common.table;

import circlebinder.common.checklist.ChecklistColor;

public class EventCircleTableForUpdate {

    private final long id;
    private final ChecklistColor checklistColor;

    public EventCircleTableForUpdate(long id, ChecklistColor checklistColor) {
        this.id = id;
        this.checklistColor = checklistColor;
    }

    long getId() {
        return id;
    }

    int getChecklistId() {
        return checklistColor.getId();
    }
}
