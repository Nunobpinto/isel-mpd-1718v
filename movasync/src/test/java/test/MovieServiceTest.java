package test;

import com.google.common.util.concurrent.RateLimiter;
import movasync.MovieService;
import movasync.MovieWebApi;
import movasync.dto.SearchItemDto;
import movasync.model.Credit;
import movasync.model.Movie;
import movasync.model.Person;
import movasync.model.SearchItem;
import org.junit.jupiter.api.Test;
import util.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MovieServiceTest {

    @Test
    public void testSearchMovieInSinglePage() {
//        RateLimiter rateLimiter = RateLimiter.create(2.0);
//        WriteFileDecorator writeFileDecorator = new WriteFileDecorator(new HttpRequest()
//                .compose(System.out::println)
//                .compose(__ -> rateLimiter.acquire()));
//        MovieService movieService = new MovieService(new MovieWebApi(writeFileDecorator));
        MovieService movieService = new MovieService(new MovieWebApi(new FileRequest().compose(System.out::println)));
        List<SearchItem> movies = movieService.search("War Games")
                .join()
                .collect(Collectors.toList());
        SearchItem m = movies.stream().findFirst().get();
        assertEquals("War Games: The Dead Code", m.getTitle());
        assertEquals(6, movies.size());
    }

    @Test
    public void testSearchMovieManyPages() {
//        RateLimiter rateLimiter = RateLimiter.create(1.0);
//        WriteFileDecorator writeFileDecorator = new WriteFileDecorator(new HttpRequest()
//                .compose(System.out::println)
//                .compose(__ -> rateLimiter.acquire()));
//        MovieService movieService = new MovieService(new MovieWebApi(writeFileDecorator));
        MovieService movieService = new MovieService(new MovieWebApi(new FileRequest().compose(System.out::println)));

        List<SearchItem> movies = movieService.search("galo").join().collect(Collectors.toList());
        SearchItem galoSengen = movies.stream()
                .filter(m -> m.getTitle().equals("Galo Sengen"))
                .findFirst()
                .get();
        assertEquals(51, movies.size());
    }

    @Test
    public void testMovieDbApiGetActor() {
//        int[] count = {0};
//        RateLimiter rateLimiter = RateLimiter.create(2.0);
//        WriteFileDecorator writeFileDecorator = new WriteFileDecorator(new HttpRequest()
//                .compose(System.out::println)
//                .compose(__ -> rateLimiter.acquire())
//                .compose(__ -> count[0]++));
//        MovieWebApi movieServiceApi = new MovieWebApi(writeFileDecorator);
        int[] count = {0};
        IRequest req = new FileRequest()
                .compose(System.out::println)
                .compose(__ -> count[0]++);
        MovieWebApi movieServiceApi = new MovieWebApi(req);

        SearchItemDto[] actorMovs = movieServiceApi.getPersonCreditsCast(1461).join();
        assertNotNull(actorMovs);
        assertEquals("O Brother, Where Art Thou?", actorMovs[0].getTitle());
        assertEquals(1, count[0]);
    }

    @Test
    public void testSearchMovieThenActorsThenMoviesAgain() {
//                RateLimiter rateLimiter = RateLimiter.create(5.0);
//        WriteFileDecorator writeFileDecorator = new WriteFileDecorator(new HttpRequest()
//                .compose(System.out::println)
//                .compose(__ -> rateLimiter.acquire()));
//        MovieService movieService = new MovieService(new MovieWebApi(writeFileDecorator));
        IRequest req = new FileRequest()
                .compose(System.out::println);
        MovieService movieService = new MovieService(new MovieWebApi(req));

        List<SearchItem> movies = movieService.search("Furious").join().collect(Collectors.toList());
        assertEquals(43, movies.size());

        SearchItem furious = movies.stream()
                .filter(m -> m.getTitle().equals("Furious"))
                .findFirst()
                .get();
        assertEquals(167104, furious.getId());
        assertEquals("Furious", furious.getTitle());

        Movie furiousDetails = furious.getDetails().join();
        assertEquals("Furious", furiousDetails.getOriginalTitle());
        assertEquals("", furiousDetails.getTagline());

        List<Credit> furiousCredits = furiousDetails.getCredits().join().collect(Collectors.toList());
        assertEquals("Simon Rhee", furiousCredits.stream().findFirst().get().getName());
        Stream<Credit> iter = furiousCredits.stream().skip(2);
        assertEquals("Howard Jackson ",
                iter.findFirst().get().getName());
    }
}

