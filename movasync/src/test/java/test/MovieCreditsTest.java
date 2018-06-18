package test;

import com.google.common.util.concurrent.RateLimiter;
import movasync.MovieService;
import movasync.MovieWebApi;
import movasync.model.Credit;
import org.junit.jupiter.api.Test;
import util.FileRequest;
import util.HttpRequest;
import util.WriteFileDecorator;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovieCreditsTest {

    @Test
    public void testMovieCreditsMergedActor() {
//                RateLimiter rateLimiter = RateLimiter.create(2.0);
//        WriteFileDecorator writeFileDecorator = new WriteFileDecorator(new HttpRequest()
//                .compose(System.out::println)
//                .compose(__ -> rateLimiter.acquire()));
//        MovieService movieApi = new MovieService(new MovieWebApi(writeFileDecorator));
        MovieService movieApi = new MovieService(new MovieWebApi(new FileRequest()
                .compose(System.out::println)
        ));
        List<Credit> credits = movieApi.getMovieCredits(489).join().collect(Collectors.toList());
        Credit res = credits
                .stream()
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
//                RateLimiter rateLimiter = RateLimiter.create(2.0);
//        WriteFileDecorator writeFileDecorator = new WriteFileDecorator(new HttpRequest()
//                .compose(System.out::println)
//                .compose(__ -> rateLimiter.acquire()));
//        MovieService movieApi = new MovieService(new MovieWebApi(writeFileDecorator));
        MovieService movieApi = new MovieService(new MovieWebApi(new FileRequest()
                .compose(System.out::println)
        ));
        Stream<Credit> req1 = movieApi.getMovieCredits(489).join();
        assertEquals(114, req1.count());
    }

}
