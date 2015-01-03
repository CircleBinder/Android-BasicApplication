package net.ichigotake.common.util;

import java.util.ArrayList;
import java.util.List;

public class TupleCollections<T1, T2> {

    public static <T1, T2> TupleCollections<T1, T2> from(List<T1> list1, List<T2> list2) {
        return new TupleCollections<>(list1, list2);
    }

    private final List<T1> list1;
    private final List<T2> list2;

    private TupleCollections(List<T1> list1, List<T2> list2) {
        this.list1 = list1;
        this.list2 = list2;
    }

    public void each(Each<Tuple<T1, T2>> function) {
        for (int i=0,size=list1.size(); i<size; i++) {
            Tuple<T1, T2> tuple = new Tuple<>(list1.get(i), list2.get(i));
            function.each(tuple);
        }
    }

    public <R> List<R> filter(Function<Tuple<T1, T2>, Optional<R>> function) {
        List<R> filtered = new ArrayList<>();
        for (int i=0,size=list1.size(); i<size; i++) {
            Tuple<T1, T2> tuple = new Tuple<>(list1.get(i), list2.get(i));
            for (R item : function.apply(tuple).asSet()) {
                filtered.add(item);
            }
        }
        return filtered;
    }

}
