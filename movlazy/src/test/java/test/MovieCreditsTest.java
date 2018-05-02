package test;

import movlazy.MovieService;
import movlazy.MovieWebApi;
import movlazy.model.Credits;
import org.junit.jupiter.api.Test;
import util.HttpRequest;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovieCreditsTest {

    @Test
    public void testMovieCredits() {
        MovieService movieApi = new MovieService(new MovieWebApi(new HttpRequest().compose(System.out::println)));
        Supplier<Stream<Credits>> credit = movieApi.getMovieCredits(860);
        Credits m = credit.get().findFirst().get();
        assertEquals("", credit);
        //assertEquals(42, movies.get().count());
    }

}
