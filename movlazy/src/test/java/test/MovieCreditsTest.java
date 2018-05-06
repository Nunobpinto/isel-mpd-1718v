package test;

import movlazy.MovieService;
import movlazy.MovieWebApi;
import movlazy.model.Credit;
import org.junit.jupiter.api.Test;
import util.FileRequest;
import util.HttpRequest;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovieCreditsTest {

    @Test
    public void testMovieCreditsMergedActor() {
        MovieService movieApi = new MovieService(new MovieWebApi(new FileRequest().compose(System.out::println)));
        Supplier<Stream<Credit>> credits = movieApi.getMovieCredits(489);
        Credit res = credits
                .get()
                .filter(c -> c.getId() == 1892)
                .findFirst()
                .get();
        System.out.println(res.toString());
        assertEquals("Matt Damon", res.getName());
        assertEquals("Writer", res.getJob());
        assertEquals("Writing", res.getDepartment());
        assertEquals("Will Hunting", res.getCharacter());
    }

    @Test void testMovieCreditsCount() {
        final int[] count = {0};
        MovieService movieApi = new MovieService(new MovieWebApi(new FileRequest().compose(System.out::println).compose(__ -> count[0]++)));
        Supplier<Stream<Credit>> req1 = movieApi.getMovieCredits(489);
        Supplier<Stream<Credit>> req2 = movieApi.getMovieCredits(489);
        long req2size = req2.get().count();
        assertEquals(114, req1.get().count());
        assertEquals(1, count[0]);
    }

}
