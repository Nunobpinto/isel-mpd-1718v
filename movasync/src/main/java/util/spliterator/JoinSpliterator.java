package util.spliterator;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class JoinSpliterator<T> extends Spliterators.AbstractSpliterator<T> {
    private final Spliterator<T> from;
    private List<T> to;
    private Spliterator<T> toSpliterator;
    private BiConsumer<T, List<T>> consumer;

    public JoinSpliterator(Spliterator<T> src, List<T> to, BiConsumer<T, List<T>> consumer) {
        super(src.estimateSize() + to.size(), DISTINCT | SIZED | NONNULL | ORDERED);
        this.from = src;
        this.to = to;
        this.consumer = consumer;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if( from.tryAdvance( item -> {
            consumer.accept(item, to);
            action.accept(item);
        }) ) {
            return true;
        } else {
            if( toSpliterator == null ) toSpliterator = to.spliterator();
            return toSpliterator.tryAdvance(action::accept);
        }
    }
}
