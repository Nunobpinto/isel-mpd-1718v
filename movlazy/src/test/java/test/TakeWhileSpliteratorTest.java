package test;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static com.google.common.primitives.Ints.asList;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static util.QueriesSpliterator.takeWhile;

public class TakeWhileSpliteratorTest {

    @Test
    public void testTakeWhile() {
        Stream<Integer> nrs = Stream.of(1, 2, 3, 3, 3, 4, 5, 6, 7, 8, 9, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1);
        Iterable<Integer> actual = () -> takeWhile(nrs, i -> i < 4).iterator();
        Iterable<Integer> expected = asList(1, 2, 3, 3, 3);
        assertIterableEquals(expected, actual);

    }

}
