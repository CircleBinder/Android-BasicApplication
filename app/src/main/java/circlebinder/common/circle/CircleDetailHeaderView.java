package circlebinder.common.circle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import circlebinder.R;
import circlebinder.common.event.Circle;

public class CircleDetailHeaderView extends LinearLayout {

    private TextView circleSpaceLabel;
    private TextView circleNameLabel;

    @SuppressWarnings("unused") // Public API
    public CircleDetailHeaderView(Context context) {
        super(context);
        initialize();
    }

    @SuppressWarnings("unused") // Public API
    public CircleDetailHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    @SuppressWarnings("unused") // Public API
    public CircleDetailHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.common_view_circle_detail_header, this, true);
        this.circleNameLabel = (TextView) view.findViewById(R.id.common_view_circle_detail_header_name);
        this.circleSpaceLabel = (TextView) view.findViewById(R.id.common_view_circle_detail_header_space);
    }

    public void setCircle(Circle circle) {
        this.circleSpaceLabel.setText(circle.getSpace().getName());
        this.circleNameLabel.setText(circle.getPenName() + "/" + circle.getName());
    }
}