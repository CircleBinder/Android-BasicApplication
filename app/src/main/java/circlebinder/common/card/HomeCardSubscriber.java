package circlebinder.common.card;

import android.content.Context;

import java.util.List;

import circlebinder.common.table.SQLite;
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
            List<HomeCard> checklists = new ChecklistCardRetriever(SQLite.getDatabase(context)).call();
            subscriber.onNext(checklists);
            subscriber.onCompleted();
        } catch (Exception e) {
            subscriber.onError(e);
        }
    }
}
