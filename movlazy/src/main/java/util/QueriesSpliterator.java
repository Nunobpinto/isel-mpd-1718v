package util;

import util.spliterator.JoinSpliterator;
import util.spliterator.TakeWhileSpliterator;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.*;

public class QueriesSpliterator {

    public static <T> Stream<T> takeWhile (Stream<T> stream, Predicate<? super T> predicate) {
        return stream(new TakeWhileSpliterator(stream.spliterator(), predicate), false);
    }

    public static <T> Stream<T> joinSeq(Stream<T> stream, List<T> list, BiConsumer<T, List<T>> action) {
        return stream(new JoinSpliterator(stream.spliterator(), list, action), false);
    }

}
