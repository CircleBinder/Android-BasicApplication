package circlebinder.common.checklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.ichigotake.common.widget.ArrayAdapter;

import circlebinder.common.event.Circle;
import circlebinder.R;

public final class ChecklistAdapter extends ArrayAdapter<Checklist, ChecklistItemViewHolder> {

    public ChecklistAdapter(Context context) {
        super(context);
    }

    @Override
    public View generateView(int position, Checklist item, LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.common_checklist_item, parent, false);
    }

    @Override
    public void bindView(int position, Checklist item, ChecklistItemViewHolder holder) {
        holder.getLabelName().setText(item.getName());
        holder.getChecklist().setImageResource(item.getChecklistColor().getDrawableResource());

        int circleSize = item.getCircleOverview().size();
        for (int i=0, size=holder.getCircles().size(); i<size; i++) {
            TextView circleView = holder.getCircles().get(i);
            if (i < circleSize) {
                Circle circle = item.getCircleOverview().get(i);
                circleView.setText(circle.getSpace().getSimpleName() + " " + circle.getName());
            } else {
                circleView.setText(" ");
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ChecklistItemViewHolder generateTag(int position, Checklist item, View convertView) {
        return new ChecklistItemViewHolder(convertView);
    }

}
