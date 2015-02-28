package net.ichigotake.common.rx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;

public class ActionSubscribe<T> implements Observable.OnSubscribe<T> {

    private final List<Binder<T>> binders;
    private final Callable<T> callable;

    public ActionSubscribe(Callable<T> callable) {
        this.binders = new ArrayList<>();
        this.callable = callable;
    }

    public ActionSubscribe<T> bind(Binder<T> binder) {
        binders.add(binder);
        return this;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        try {
            T item = callable.call();
            for (Binder<T> binder : binders) {
                binder.bind(item);
            }
            subscriber.onCompleted();
        } catch (Exception e) {
            subscriber.onError(e);
        }
    }
}
