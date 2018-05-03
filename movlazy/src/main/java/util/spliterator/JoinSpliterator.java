package util.spliterator;

import java.util.ArrayList;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class JoinSpliterator <T> extends Spliterators.AbstractSpliterator<T> {
    private final Spliterator<T> splitr;
    private ArrayList list;
    private Predicate<T> predicate;

    public JoinSpliterator(Spliterator<T> src, ArrayList list, Predicate<T> predicate) {
        super(src.estimateSize(), DISTINCT | SIZED | NONNULL | ORDERED);
        this.splitr = src;
        this.list = list;
        this.predicate = predicate;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {

        return true;

    }
}
