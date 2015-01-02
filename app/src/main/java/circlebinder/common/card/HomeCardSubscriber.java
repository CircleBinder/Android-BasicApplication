package circlebinder.common.card;

import android.content.Context;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

final class HomeCardSubscriber implements Observable.OnSubscribe<List<HomeCard>> {

    private final Context context;

    HomeCardSubscriber(Context context) {
        this.context = context;
    }

    @Override
    public void call(Subscriber<? super List<HomeCard>> subscriber) {
        try {
            List<HomeCard> checklists = new ChecklistCardCallable(context).call();
            subscriber.onNext(checklists);
            subscriber.onCompleted();
        } catch (Exception e) {
            subscriber.onError(e);
        }
    }
}
