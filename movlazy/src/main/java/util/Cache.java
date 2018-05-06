package util;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Cache {

    public static <T> Supplier<Stream<T>> of(Supplier<Stream<T>> dataSrc) {
        final Spliterator<T> src = dataSrc.get().spliterator(); // !!!maybe it should be lazy and memorized!!!
        final Recorder<T> rec = new Recorder<>(src);
        return () -> {
            // CacheIterator starts on index 0 and reads data from src or
            // from an internal cache of Recorder.
            Spliterator<T> iter = rec.cacheIterator();
            return StreamSupport.stream(iter, false);
        };
    }

    static class Recorder<T> {
        final Spliterator<T> src;
        final List<T> cache = new ArrayList<>();
        final long estimateSize;
        boolean hasNext = true;

        Recorder(Spliterator<T> src) {
            this.src = src;
            this.estimateSize = src.estimateSize();
        }

        synchronized boolean getOrAdvance(
                final int index,
                Consumer<? super T> cons) {
            if (index < cache.size()) {
                // If it is in cache then just get if from the corresponding index.
                cons.accept(cache.get(index));
                return true;
            } else if (hasNext)
                // If not in cache then advance the src iterator
                hasNext = src.tryAdvance(item -> {
                    cache.add(item);
                    cons.accept(item);
                });
            return hasNext;
        }

        Spliterator<T> cacheIterator() {
            return new Spliterators.AbstractSpliterator<T>(
                    estimateSize, src.characteristics()
            ) {
                int index = 0;

                public boolean tryAdvance(Consumer<? super T> cons) {
                    return getOrAdvance(index++, cons);
                }

                public Comparator<? super T> getComparator() {
                    return src.getComparator();
                }
            };
        }
    }
}
