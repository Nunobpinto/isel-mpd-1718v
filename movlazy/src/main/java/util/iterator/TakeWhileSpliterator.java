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

    protected TakeWhileSpliterator(Spliterator<T> src, int additionalCharacteristics) {
        super(src.estimateSize(), additionalCharacteristics);
        this.splitr = src;
        stillGoing = true;
    }

    public <T> Spliterator<T> takeWhile(Spliterator<T> splitr, Predicate<? super T> predicate) {

    }

    public <T> Stream<T> takeWhile(Stream<T> stream, Predicate<? super T> predicate) {
        return StreamSupport.stream(takeWhile(stream.spliterator(), predicate), false);
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (stillGoing) {
            boolean hadNext = splitr.tryAdvance(elem -> {
                if (predicate.test(elem)) {
                    consumer.accept(elem);
                } else {
                    stillGoing = false;
                }
            });
            return hadNext && stillGoing;
        }
        return false;
    }
}
