package util;

import util.spliterator.JoinSpliterator;
import util.spliterator.TakeWhileSpliterator;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.*;

public class QueriesSpliterator {

    /**
     * Returns a new Spliterator consisting of the longest prefix of elements
     * taken from the src Stream that match the given predicate.
     */
    public static <T> Stream<T> takeWhile (Stream<T> stream, Predicate<? super T> predicate) {
        return stream(new TakeWhileSpliterator(stream.spliterator(), predicate), false);
    }

    public static <T> Stream<T> joinSeq(Stream<T> stream, ArrayList list, Predicate<? super T> predicate) {
        return stream(new JoinSpliterator(stream.spliterator(), list, predicate), false);
    }

}
