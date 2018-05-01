package util.iterator;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TakeWhileSpliterator<T> extends Spliterators.AbstractSpliterator<T> {
    private boolean stillGoing;
    private final Spliterator<T> splitr;
    private Predicate<T> predicate;

    public TakeWhileSpliterator(Spliterator<T> src, Predicate<T> predicate) {
        super(src.estimateSize(), DISTINCT | SIZED | NONNULL | ORDERED);
        this.splitr = src;
        this.predicate = predicate;
        stillGoing = true;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (stillGoing) {
            boolean hadNext = splitr.tryAdvance(elem -> {
                if (predicate.test(elem)) {
                    action.accept(elem);
                } else {
                    stillGoing = false;
                }
            });
            return hadNext && stillGoing;
        }
        return false;
    }
}
