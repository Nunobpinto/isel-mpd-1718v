package test;

import movlazy.MovieService;
import movlazy.MovieWebApi;
import movlazy.model.CastItem;
import movlazy.model.SearchItem;
import org.junit.jupiter.api.Test;
import util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static util.Queries.filter;
import static util.Queries.skip;

public class MovieServiceTestForFurious {

    @Test
    public void testSearchMovieInSinglePage() {
        MovieService movieApi = new MovieService(new MovieWebApi(new FileRequest().compose(System.out::println)));
        Iterable<SearchItem> movies = movieApi.search("Furious");
        SearchItem m = movies.iterator().next();
        assertEquals("Furious 7", m.getTitle());
        assertEquals(42, Queries.count(movies));
    }

    @Test
    public void testSearchMovieManyPages() {
        int[] count = {0};
        IRequest req = new FileRequest()
                .compose(System.out::println)
                .compose(__ -> count[0]++);

        MovieService movieApi = new MovieService(new MovieWebApi(req));
        Iterable<SearchItem> movies = movieApi.search("batman");
        assertEquals(0, count[0]);
        SearchItem batmanAndrobin = filter(m -> m.getTitle().equals("Batman & Robin"), movies)
                .iterator()
                .next();
        assertEquals(1, count[0]);
        assertEquals(108, Queries.count(movies));
        assertEquals(8, count[0]);
    }

    @Test
    public void testMovieDbApiGetActor() {
        int[] count = {0};
        IRequest req = new FileRequest()
                .compose(System.out::println)
                .compose(__ -> count[0]++);

        MovieService movieServiceApi = new MovieService(new MovieWebApi(req));
        Iterable<SearchItem> actorMovs = movieServiceApi.getActorCreditsCast(1461);
        assertNotNull(actorMovs);
        assertEquals("O Brother, Where Art Thou?", actorMovs.iterator().next().getTitle());
        assertEquals(1, count[0]);
    }

    @Test
    public void testSearchMovieThenActorsThenMoviesAgain() {
        final int[] count = {0};
        IRequest req = new FileRequest()
                .compose(__ -> count[0]++)
                .compose(System.out::println);

        MovieService movieService = new MovieService(new MovieWebApi(req));

        Iterable<SearchItem> movies = movieService.search("Furious");
        assertEquals(42, Queries.count(movies));
        assertEquals(4, count[0]);
        /**
         * Iterable<SearchItem> is Lazy and without cache.
         */
        SearchItem furious = filter(
                m -> m.getTitle().equals("Furious"),
                movies)
                .iterator()
                .next();
        assertEquals(5, count[0]);
        assertEquals(167104, furious.getId());
        assertEquals("Furious", furious.getTitle());
        assertEquals(5, count[0]);
        /**
         * getDetails() relation SearchItem ---> Movie is Lazy and supported on Supplier<Movie> with Cache
         */
        assertEquals("Furious", furious.getDetails().getOriginalTitle());
        assertEquals(6, count[0]);
        assertEquals("", furious.getDetails().getTagline());
        assertEquals(6, count[0]);
        /**
         * getCast() relation Movie --->* CastItem is Lazy and
         * supported on Supplier<List<CastItem>> with Cache
         */
        Iterable<CastItem> furiousCast = furious.getDetails().getCast();
        assertEquals(7, count[0]);
        assertEquals("Simon Rhee",
                furiousCast.iterator().next().getName());
        assertEquals(7, count[0]);
        assertEquals("Howard Jackson ",
                skip(furiousCast, 2).iterator().next().getName());
        assertEquals(7, count[0]);
        /**
         * CastItem ---> Actor is Lazy and with Cache for Person but No cache for actor credits
         */
        CastItem simon = furious.getDetails().getCast().iterator().next();
        assertEquals(7, count[0]);
        assertEquals("San Jose, California, USA",
                simon.getActor().getPlaceOfBirth());
        assertEquals(8, count[0]);
        assertEquals("San Jose, California, USA",
                simon.getActor().getPlaceOfBirth());
        assertEquals(8, count[0]);
        assertEquals("Best of the Best 2",
                simon.getActor().getMovies().iterator().next().getTitle());
        assertEquals(9, count[0]);
        assertEquals("Best of the Best 2",
                simon.getActor().getMovies().iterator().next().getTitle());
        assertEquals(10, count[0]);

        /**
         * Check Cache from the beginning
         */
        assertEquals("San Jose, California, USA",
                movieService.getMovie(167104).getCast().iterator().next().getActor().getPlaceOfBirth());
        assertEquals(10, count[0]);
        /*
         * Now get a new Film
         */
        assertEquals("Apocalypse Now",
                movieService.getMovie(238).getCast().iterator().next().getActor().getMovies().iterator().next().getTitle());
        assertEquals(14, count[0]);
    }

    @Test
    public void testSearchMovieWithManyPages() {
        final int[] count = {0};
        IRequest req = new FileRequest()
                .compose(__ -> count[0]++)
                .compose(System.out::println);

        MovieService movapi = new MovieService(new MovieWebApi(req));

        Iterable<SearchItem> vs = movapi.search("where");
        assertEquals(567, Queries.count(vs));
        assertEquals(30, count[0]);
    }
}

