package net.ichigotake.common.rx;

import java.util.concurrent.Callable;

import rx.Observable;

public class ObservableBuilder<T> {

    public static <T> ObservableBuilder<T> from(Callable<T> callable) {
        return new ObservableBuilder<>(callable);
    }

    private final ActionSubscribe<T> subscribe;

    private ObservableBuilder(Callable<T> callable) {
        this.subscribe = new ActionSubscribe<>(callable);
    }

    public ObservableBuilder<T> bind(Binder<T> binder) {
        subscribe.bind(binder);
        return this;
    }

    public Observable<T> create() {
        return Observable.create(subscribe);
    }

}
