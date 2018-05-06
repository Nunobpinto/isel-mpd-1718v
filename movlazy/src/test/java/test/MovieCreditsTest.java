package test;

import movlazy.MovieService;
import movlazy.MovieWebApi;
import movlazy.model.Credit;
import org.junit.jupiter.api.Test;
import util.FileRequest;

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

    @Test
    public void testMovieCreditsCount() {
        MovieService movieApi = new MovieService(new MovieWebApi(new FileRequest().compose(System.out::println)));
        Supplier<Stream<Credit>> req1 = movieApi.getMovieCredits(489);
        assertEquals(114, req1.get().count());
    }

}
