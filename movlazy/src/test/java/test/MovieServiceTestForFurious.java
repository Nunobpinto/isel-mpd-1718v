package test;

import movlazy.MovieService;
import movlazy.MovieWebApi;
import movlazy.dto.SearchItemDto;
import movlazy.model.Credit;
import movlazy.model.SearchItem;
import org.junit.jupiter.api.Test;
import util.*;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MovieServiceTestForFurious {

    @Test
    public void testSearchMovieInSinglePage() {
        MovieService movieApi = new MovieService(new MovieWebApi(new FileRequest().compose(System.out::println)));
        Supplier<Stream<SearchItem>> movies = movieApi.search("Furious");
        SearchItem m = movies.get().findFirst().get();
        assertEquals("Furious 7", m.getTitle());
        assertEquals(42, movies.get().count());
    }

    @Test
    public void testSearchMovieManyPages() {
        int[] count = {0};
        IRequest req = new FileRequest()
                .compose(System.out::println)
                .compose(__ -> count[0]++);

        MovieService movieApi = new MovieService(new MovieWebApi(req));
        Supplier<Stream<SearchItem>> movies = movieApi.search("batman");
        assertEquals(0, count[0]);
        SearchItem batmanAndrobin = movies.get()
                .filter(m -> m.getTitle().equals("Batman & Robin"))
                .findFirst()
                .get();
        assertEquals(1, count[0]);
        assertEquals(108, movies.get().count());
        assertEquals(8, count[0]);
    }

    @Test
    public void testMovieDbApiGetActor() {
        int[] count = {0};
        IRequest req = new FileRequest()
                .compose(System.out::println)
                .compose(__ -> count[0]++);

        MovieWebApi movieServiceApi = new MovieWebApi(req);
        SearchItemDto [] actorMovs = movieServiceApi.getPersonCreditsCast(1461);
        assertNotNull(actorMovs);
        assertEquals("O Brother, Where Art Thou?", actorMovs[0].getTitle());
        assertEquals(1, count[0]);
    }

    @Test
    public void testSearchMovieThenActorsThenMoviesAgain() {
        final int[] count = {0};
        IRequest req = new FileRequest()
                .compose(__ -> count[0]++)
                .compose(System.out::println);

        MovieService movieService = new MovieService(new MovieWebApi(req));

        Supplier<Stream<SearchItem>> movies = movieService.search("Furious");
        assertEquals(42, movies.get().count());
        assertEquals(4, count[0]);

        SearchItem furious = movies.get()
                .filter(m -> m.getTitle().equals("Furious"))
                .findFirst()
                .get();
        assertEquals(5, count[0]);
        assertEquals(167104, furious.getId());
        assertEquals("Furious", furious.getTitle());
        assertEquals(5, count[0]);

        assertEquals("Furious", furious.getDetails().getOriginalTitle());
        assertEquals(7, count[0]);
        assertEquals("", furious.getDetails().getTagline());
        assertEquals(7, count[0]);

        Supplier<Stream<Credit>> furiousCast = furious.getDetails().getCast();
        assertEquals(7, count[0]);
        assertEquals("Simon Rhee",
                furiousCast.get().findFirst().get().getName());
        assertEquals(7, count[0]);
        Stream<Credit> iter = furiousCast.get().skip(2);
        assertEquals("Howard Jackson ",
                iter.findFirst().get().getName());
        assertEquals(7, count[0]);

        Credit simon = furious.getDetails().getCast().get().findFirst().get();
        assertEquals(7, count[0]);
        assertEquals("San Jose, California, USA",
                simon.getActor().getPlaceOfBirth());
        assertEquals(8, count[0]);
        assertEquals("San Jose, California, USA",
                simon.getActor().getPlaceOfBirth());
        assertEquals(8, count[0]);
        assertEquals("Best of the Best 2",
                simon.getActor().getMovies().get().findFirst().get().getTitle());
        assertEquals(9, count[0]);
        assertEquals("Best of the Best 2",
                simon.getActor().getMovies().get().findFirst().get().getTitle());
        assertEquals(10, count[0]);

        assertEquals("San Jose, California, USA",
                movieService.getMovie(167104).getCast().get().findFirst().get().getActor().getPlaceOfBirth());
        assertEquals(10, count[0]);

        assertEquals("Apocalypse Now",
                movieService.getMovie(238).getCast().get().findFirst().get().getActor().getMovies().get().findFirst().get().getTitle());
        assertEquals(14, count[0]);
    }

    @Test
    public void testSearchMovieWithManyPages() {
        final int[] count = {0};
        IRequest req = new FileRequest()
                .compose(__ -> count[0]++)
                .compose(System.out::println);

        MovieService movapi = new MovieService(new MovieWebApi(req));

        Stream<SearchItem> vs = movapi.search("where").get();
        assertEquals(567, vs.count());
        assertEquals(30, count[0]);
    }
}

