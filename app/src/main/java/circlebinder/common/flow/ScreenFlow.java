package circlebinder.common.flow;

import flow.Backstack;
import flow.Flow;

public interface ScreenFlow {
    
    Screen createFirstScreen();

    void onAfterGoing(Backstack nextBackstack, Flow.Direction direction, Flow.Callback callback);

}
